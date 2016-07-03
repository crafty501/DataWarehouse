package analiser;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import data.DB2ConnectionManager;

public class AnaliserJFrame extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
//	String query = "select DECODE(art.name, 1, art.name) AS alias"
//	+ " from sales as sale"
//	+ " join shop as shop"
//	+ " on sale.shop = shop.name"
//	+ " join artikel as art"
//	+ " on sale.artikel = art.name";
	String query = "select art.name "
			+ "from Artikel as art";
	
//	String query = "select sale.artikel,art.preis, art.gruppe, art.familie, art.kategorie ,sale.shop, sale.datum, sale.verkauft, sale.umsatz"
//			+ " from sales as sale"
//			+ " join shop as shop"
//			+ " on sale.shop = shop.name"
//			+ " join artikel as art"
//			+ " on sale.artikel = art.name";
	
	DB2ConnectionManager mgr;
	
	public AnaliserJFrame(DB2ConnectionManager mgr) {
		this.mgr = mgr;
		this.setLayout(new FlowLayout());
		JButton start = new JButton("start");
		this.add(start);
		
		
		ArrayList<String> artikelNames = getArtikelNames();
		String decode = generateDecodeStringNametoVerkauft(artikelNames);
		String query = "select shop.stadt,sale.datum"+decode
				+ " from sales as sale"
				+ " join shop as shop"
				+ " on sale.shop = shop.name"
				+ " join artikel as art"
				+ " on sale.artikel = art.name"
				+ " group by rollup (shop.stadt, sale.datum)"
				+ " order by shop.stadt, sale.datum";
				
				
		
		System.out.println(decode);
		
//		String queryx = "select shop.stadt, sale.datum, MAX(DECODE(art.name, 'LG RH-T 298',sale.verkauft)) AS \"test test\""
//				+ " from sales as sale"
//				+ " join shop as shop"
//				+ " on sale.shop = shop.name"
//				+ " join artikel as art"
//				+ " on sale.artikel = art.name"
//				+ " group py shop.stadt";
		
		start.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ResultSet rs;
				try {
					System.out.println("query request");
					rs = mgr.sendQuery(query,true);
					System.out.println("query output");
					JTable table = new JTable(buildTableModel(rs));
					table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
					JScrollPane jScrollPane = new JScrollPane(table,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
					UIManager.put("OptionPane.minimumSize",new Dimension(1024,768));
					JOptionPane.showMessageDialog(getFocusOwner(),jScrollPane);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		
		
		this.setSize(800, 600);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
private String generateDecodeStringNametoVerkauft(ArrayList<String> artikelNames) {
	String decode = "";
	for (String name : artikelNames) {
		String qurey = ",SUM(DECODE(art.name,'"+name+"',sale.verkauft)) as \""+name+"\"";
		decode = decode + qurey;
	}
	return decode;
}
	
	
private ArrayList<String> getArtikelNames(){
		String getArikelNames = "select name from artikel";
		ResultSet rs;
		ArrayList<String> artikelNames = new ArrayList<String>();
		try {
			
			rs = mgr.sendQuery(getArikelNames, true);
			while (rs.next()){
				String name = rs.getString(1);
				System.out.println(name);
				artikelNames.add(name);
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return artikelNames;
	}
	
	
	
	
	private static DefaultTableModel buildTableModel(ResultSet rs)
	        throws SQLException {

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

	    return new DefaultTableModel(data, columnNames);
	}
}
