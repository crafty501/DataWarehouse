package analiser;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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

	
	String query = "select sale.artikel,art.preis, art.gruppe, art.familie, art.kategorie ,sale.shop, sale.datum, sale.verkauft, sale.umsatz"
			+ " from sales as sale"
			+ " join shop as shop"
			+ " on sale.shop = shop.name"
			+ " join artikel as art"
			+ " on sale.artikel = art.name";
	
	
	public AnaliserJFrame(DB2ConnectionManager mgr) {
		
		this.setLayout(new FlowLayout());
		JButton start = new JButton("start");
		this.add(start);
		
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
