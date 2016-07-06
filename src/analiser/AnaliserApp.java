package analiser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import data.DB2ConnectionManager;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class AnaliserApp extends Application {

	
	private DB2ConnectionManager mgr;
	
	
	private ComboBox<ArtikelDimension> artikelCombo;
	private ComboBox<ShopDimension> shopCombo;
	private ComboBox<TimeDimension> timeCombo;
	private TableView<ObservableList<String>> tableview;

	private enum ArtikelDimension {
		name, gruppe,familie, kategorie
	}

	private enum ShopDimension {
		name, stadt, region, land
	}

	private enum TimeDimension {
		DAY, MONTH, QUARTER, YEAR
	}

	public static void main(String[] args) throws ParseException {
		
		launch(args);
	}
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.mgr = new DB2ConnectionManager();
		

        
		Button start = new Button("update");
        tableview = new TableView<>();
        
        artikelCombo = new ComboBox<>(FXCollections.observableArrayList(ArtikelDimension.values()));
        shopCombo = new ComboBox<>(FXCollections.observableArrayList(ShopDimension.values()));
        timeCombo = new ComboBox<>(FXCollections.observableArrayList(TimeDimension.values()));
        
        
        artikelCombo.setPrefWidth(100);
        shopCombo.setPrefWidth(100);
        timeCombo.setPrefWidth(100);
        
        artikelCombo.setValue(ArtikelDimension.name);
        shopCombo.setValue(ShopDimension.stadt);
        timeCombo.setValue(TimeDimension.QUARTER);

        primaryStage.setTitle("Analizer");
        VBox root = new VBox(5);
        primaryStage.setScene(new Scene(root, 800, 600));
        
        
        VBox.setVgrow(tableview, Priority.ALWAYS);
        
        root.getChildren().add(tableview);
        HBox updateBox = new HBox(start);
        updateBox.setAlignment(Pos.CENTER);

        root.getChildren().add(new HBox(artikelCombo, new Label(" : Artikel Dimension")));
        root.getChildren().add(new HBox(shopCombo, new Label(" : Shop Dimension")));
        root.getChildren().add(new HBox(timeCombo, new Label(" : Zeit Dimension")));
        root.getChildren().add(updateBox);
		

		start.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				
				updateTable(tableview);
			}
		});
		
		
		primaryStage.show();
	}

	private String generateDecodeStringNametoVerkauft(ArrayList<String> artikelDimensionNames,
			String artikelDimension) {
		String decode = "";
		for (String name : artikelDimensionNames) {
			String qurey = ",SUM(DECODE(art." + artikelDimension + ",'" + name + "',sale.verkauft)) as \"" + name
					+ "\"";
			decode = decode + qurey;
		}
		return decode;
	}
	

	private ArrayList<String> getArtikelDimensionNames(String artikelDim) {
		String getArikelNames = "select DISTINCT " + artikelDim + " from artikel";
		ResultSet rs;
		ArrayList<String> dimName = new ArrayList<String>();
		try {

			rs = mgr.sendQuery(getArikelNames, true);
			while (rs.next()) {
				String name = rs.getString(1);
				//System.out.println(name);
				dimName.add(name);

			}
		} catch (SQLException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return dimName;
	}
	
private void updateTable(TableView<ObservableList<String>> tableview) {
		
		String artikelDimension = artikelCombo.getValue().name();
		String shopDimension = shopCombo.getValue().name();
		String timeDimension = timeCombo.getValue().name();
		
		ArrayList<String> artikelNames = getArtikelDimensionNames(artikelDimension);
		String decode = generateDecodeStringNametoVerkauft(artikelNames, artikelDimension);

		
		String query = "select shop." + shopDimension + "," + timeDimension + "(sale.datum) as " + timeDimension + ""
				+ decode + " from sales as sale" + " join shop as shop" + " on sale.shop = shop.name"
				+ " join artikel as art" + " on sale.artikel = art.name" + " group by rollup (shop." + shopDimension
				+ ", " + timeDimension + "(sale.datum))" + " order by shop." + shopDimension + ", " + timeDimension
				+ "(sale.datum)";
		
		ResultSet rs;
		
		try {
			rs = mgr.sendQuery(query, true);
		
			insertTableData(rs);
			
			rs.close();
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	private void insertTableData(ResultSet rs) throws SQLException {

		tableview.getItems().clear();
		tableview.getColumns().clear();
		
		ObservableList<TableColumn<ObservableList<String>, String>> colums = FXCollections.observableArrayList();
		ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
		
		
		//generate columns
		for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
            final int j = i;    
            
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(rs.getMetaData().getColumnName(i+1));
            
            col.setPrefWidth(100);
            col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList<String>,String>,ObservableValue<String>>(){                    
                
            	public ObservableValue<String> call(CellDataFeatures<ObservableList<String>, String> param) {                                                                                              

            		if(param.getValue().get(j) != null) {
                		return new SimpleStringProperty(param.getValue().get(j).toString());                        
                	}
                	return null;
                }                    
            });
            colums.add(col);
            tableview.getColumns().add(col);
        }
		

        //gererate data

		String last = "";
        while(rs.next()){
            //Iterate Row
        	
            ObservableList<String> row = FXCollections.observableArrayList();
            for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
            	
            	String entry = rs.getString(i);
            	
            	if (i == 1) {
            		if (last.equals(entry)) {
            			row.add("--"+entry);
            			continue;
            		}
            		last = entry;
            	}
            	
            	if (entry != null) {
            		row.add(entry);
            	} else {
            		row.add("Summe");
            	}
            }
            
            data.add(row);
        }
        
        //tableview.getColumns().addAll(colums);
        tableview.setItems(data);
	}

}