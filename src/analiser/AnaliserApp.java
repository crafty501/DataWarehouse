package analiser;

import java.awt.Dimension;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import data.DB2ConnectionManager;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class AnaliserApp extends Application {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	DB2ConnectionManager mgr;
	
	private String artikelDimension;
	private String shopDimension;
	private String timeDimension;
	
	ComboBox artikelCombo;
	ComboBox shopCombo;
	ComboBox timeCombo;

	private enum ArtikelDimension {
		name, familie, gruppe, kategorie
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
	
	Stage primaryStage;
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		
		this.mgr = new DB2ConnectionManager();
		
		artikelDimension = ArtikelDimension.name.name();
		shopDimension = ShopDimension.stadt.name();
		timeDimension = TimeDimension.QUARTER.name();
		
		artikelCombo = new ComboBox(FXCollections.observableArrayList(ArtikelDimension.values()));
		shopCombo = new ComboBox(FXCollections.observableArrayList(ShopDimension.values()));
		timeCombo = new ComboBox(FXCollections.observableArrayList(TimeDimension.values()));
		
		
		
		StackPane root = new StackPane();
        
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
        
		
        Button start = new Button("start");
        
		root.getChildren().add(start);
		
		artikelCombo.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				artikelDimension = newValue;
			}
		});

		
		shopCombo.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				shopDimension = newValue;
			}
		});
		
		
		timeCombo.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				timeDimension = newValue;
			}
		});
		
		

		start.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				showTable();
			}
		});
		
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
	
	
	private void showTable() {
		
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
		
			
			TableView tableview = new TableView<>();
			insertTableData(rs, tableview);
			
			
			
			
			final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(primaryStage);
            VBox dialogVbox = new VBox(3);
            dialogVbox.getChildren().add(tableview);
            
            dialogVbox.getChildren().add(artikelCombo);
            dialogVbox.getChildren().add(timeCombo);
            dialogVbox.getChildren().add(shopCombo);
            Scene dialogScene = new Scene(dialogVbox, 300, 200);
            dialog.setScene(dialogScene);
            dialog.show();
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	
	}

	private ArrayList<String> getArtikelDimensionNames(String artikelDim) {
		String getArikelNames = "select DISTINCT " + artikelDim + " from artikel";
		ResultSet rs;
		ArrayList<String> dimName = new ArrayList<String>();
		try {

			rs = mgr.sendQuery(getArikelNames, true);
			while (rs.next()) {
				String name = rs.getString(1);
				System.out.println(name);
				dimName.add(name);

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dimName;
	}

	private void insertTableData(ResultSet rs, TableView<ObservableList<String>> tableview) throws SQLException {

		
		ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
		
		for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
            //We are using non property style for making dynamic table
            final int j = i;                
            TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
            
            col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
                
            	public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {                                                                                              

            		if(param.getValue().get(j) != null) {
                		return new SimpleStringProperty(param.getValue().get(j).toString());                        
                	}
                	return null;
                }                    
            });
            
            tableview.getColumns().addAll(col); 
            
        }

        /********************************
         * Data added to ObservableList *
         ********************************/
		String last = "";
        while(rs.next()){
            //Iterate Row
        	
            ObservableList<String> row = FXCollections.observableArrayList();
            for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
            	
            	if (i == 1) {
            		if (last.equals(rs.getString(i))) {
            			row.add(null);
            			continue;
            		}
            		last = rs.getString(i);
            	}
            	
            	row.add(rs.getString(i));
            }
            
            
            
            data.add(row);
        }
        
        tableview.setItems(data);
        
        
	}

}