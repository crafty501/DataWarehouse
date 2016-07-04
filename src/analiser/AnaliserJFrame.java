package analiser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import antlr.collections.List;
import data.DB2ConnectionManager;

public class AnaliserJFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	DB2ConnectionManager mgr;
	
	private String artikelDimension;
	private String shopDimension;
	private String timeDimension;
	
	JComboBox artikelCombo;
	JComboBox shopCombo;
	JComboBox timeCombo;

	JPanel dimpanel;
	JButton click_mich;
	JTable table;

	JProgressBar progressBar;
	
	private enum ArtikelDimension {
		name, familie, gruppe, kategorie
	}

	private enum ShopDimension {
		name, stadt, region, land
	}

	private enum TimeDimension {
		DAY, MONTH, QUARTER, YEAR
	}

	public AnaliserJFrame(DB2ConnectionManager mgr) {
		this.mgr = mgr;
		
		artikelDimension = ArtikelDimension.name.name();
		shopDimension = ShopDimension.stadt.name();
		timeDimension = TimeDimension.QUARTER.name();
		
		artikelCombo = new JComboBox(ArtikelDimension.values());
		shopCombo = new JComboBox(ShopDimension.values());
		timeCombo = new JComboBox(TimeDimension.values());
		
		artikelCombo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				artikelDimension = artikelCombo.getSelectedItem().toString();
			}
		});
		
		shopCombo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				shopDimension = shopCombo.getSelectedItem().toString();
			}
		});
		
		timeCombo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				timeDimension = timeCombo.getSelectedItem().toString();
				
			}
		});
		
	
		showTable();

		this.setSize(800, 600);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
	
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
		
		//System.out.println(query);
		
		ResultSet rs;
		try {
			System.out.println("query request");
			rs = mgr.sendQuery(query, true);
			System.out.println("query output");
			
			generateGui(rs);
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	
	}
	
	private void  generateGui(ResultSet rs) throws SQLException{
		//JFrame p = new JFrame();
		this.setLayout(new BorderLayout());
		
		progressBar = new JProgressBar();
		progressBar.setMinimum(0);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		this.add(progressBar,BorderLayout.SOUTH);
		
		
		
		table = new JTable(buildTableModel(rs));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		JScrollPane jScrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		UIManager.put("OptionPane.minimumSize", new Dimension(1024, 600));
		
		dimpanel = new JPanel();
		dimpanel.setLayout(new BoxLayout(dimpanel, BoxLayout.Y_AXIS));
		//dimpanel.setLayout(new FlowLayout());
		dimpanel.add(artikelCombo);
		dimpanel.add(shopCombo);
		dimpanel.add(timeCombo);
		
		click_mich = new JButton();
		click_mich.setText("OK");
		click_mich.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						progressBar.setValue(0);
						// TODO Auto-generated method stub
						ArrayList<String> artikelNames = getArtikelDimensionNames(artikelDimension);
						String decode = generateDecodeStringNametoVerkauft(artikelNames, artikelDimension);

						
						String query = "select shop." + shopDimension + "," + timeDimension + "(sale.datum) as " + timeDimension + ""
								+ decode + " from sales as sale" + " join shop as shop" + " on sale.shop = shop.name"
								+ " join artikel as art" + " on sale.artikel = art.name" + " group by rollup (shop." + shopDimension
								+ ", " + timeDimension + "(sale.datum))" + " order by shop." + shopDimension + ", " + timeDimension
								+ "(sale.datum)";
						
						//System.out.println(query);
						
						ResultSet rs;
						try {
							System.out.println("query request");
							rs = mgr.sendQuery(query, true);
							System.out.println("query output");
							
							updateTable(rs);
							
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
				});
				
				t.start();
					}
				});
		dimpanel.add(click_mich);
		//JOptionPane.showMessageDialog(getFocusOwner(), p);
		this.add(dimpanel,BorderLayout.WEST);
		this.add(jScrollPane,BorderLayout.CENTER);
		this.setVisible(true);

						
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

	private static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {

		ResultSetMetaData metaData = rs.getMetaData();
		// names of columns
		Vector<String> columnNames = new Vector<String>();
		int columnCount = metaData.getColumnCount();
		for (int column = 1; column <= columnCount; column++) {
			columnNames.add(metaData.getColumnName(column));
		}

		// data of the table
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		while (rs.next()) {
			Vector<Object> vector = new Vector<Object>();
			for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
				vector.add(rs.getObject(columnIndex));
			}
			data.add(vector);
		}

		//TODO
		for(int i = 0; i < 10000; i ++ ){
			Vector<Object> vector = new Vector<Object>();
			for(int j = 0 ; j < 10000; j++){
				vector.add("");
			}
			data.add(vector);
		}
		
		return new DefaultTableModel(data, columnNames);
	}
	
	private  void updateTable(ResultSet rs) throws SQLException {

		
		
		//clear all data in the table 
		for(int i = 0 ; i < table.getRowCount() ; i++){
			for(int j = 0 ; j < table.getColumnCount(); j++){
			table.setValueAt("", i, j);
			}
		}
		
		
		
		ResultSetMetaData metaData = rs.getMetaData();
		// names of columns
		int columnCount = metaData.getColumnCount();
		table.getColumnModel().getColumn(0).setHeaderValue(shopDimension);
		table.getColumnModel().getColumn(1).setHeaderValue(timeDimension);
		
		

		// data of the table
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		while (rs.next()) {
			Vector<Object> vector = new Vector<Object>();
			for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
				vector.add(rs.getObject(columnIndex));	
			}
			data.add(vector);
			
			
		}
		
		int z = data.size() * data.get(1).size();
		progressBar.setMaximum(z);
		
		for(int i = 0 ; i < data.size(); i ++){
			
			for(int j = 0 ; j < data.get(i).size(); j++){
				Object o = data.get(i).get(j);
				String value = "";
				if(o != null){
					value = o.toString();
				}else{
					value = "SUMME";
				}
				table.setValueAt(value, i, j);
				progressBar.setValue(progressBar.getValue() +1);
			}
		}

		table.repaint();
		table.getTableHeader().repaint();
	}

	// String query2 = "select shop.stadt,sale.datum"+decode
	// + " from sales as sale"
	// + " join shop as shop"
	// + " on sale.shop = shop.name"
	// + " join artikel as art"
	// + " on sale.artikel = art.name"
	// + " group by rollup (shop.stadt, sale.datum)"
	// + " order by shop.stadt, sale.datum";
	//
	//
	// String query3 = "select shop.land,sale.datum"+decode
	// + " from sales as sale"
	// + " join shop as shop"
	// + " on sale.shop = shop.name"
	// + " join artikel as art"
	// + " on sale.artikel = art.name"
	// + " group by rollup (shop.land, sale.datum)"
	// + " order by shop.land, sale.datum";
	//
	//
	// String query4 = "select shop.land,sale.datum"+decode
	// + " from sales as sale"
	// + " join shop as shop"
	// + " on sale.shop = shop.name"
	// + " join artikel as art"
	// + " on sale.artikel = art.name"
	// + " group by rollup (shop.land, sale.datum)"
	// + " order by shop.land, sale.datum";
	//
	//
	//
	// String queryx = "select shop.stadt, sale.datum, MAX(DECODE(art.name, 'LG
	// RH-T 298',sale.verkauft)) AS \"test test\""
	// + " from sales as sale"
	// + " join shop as shop"
	// + " on sale.shop = shop.name"
	// + " join artikel as art"
	// + " on sale.artikel = art.name"
	// + " group py shop.stadt";
}
