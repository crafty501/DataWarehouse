package kolja.query;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JTable;

import data.DB2ConnectionManager;

public class QueryThread extends Thread{
	
	
	String Region,Product,startDate,endDate;
    int zeile,i;
    boolean update;
    JTable table;
    String[][] data;
    DB2ConnectionManager mgr;
    
private ResultSet MakeQuery(String Region,String Product,String StartDate ,String EndDate){
		
		String Anfrage = "SELECT sum(VERKAUFT) "
				+ "FROM SHOP S, Artikel A, SALES SA "
				+ "WHERE "
				+ "A.NAME='"+Product+"' "
				+ "AND REGION='"+Region+"' "
				+ "AND DATUM > '"+StartDate+"' "
				+ "AND DATUM < '"+EndDate+"'"
				+ "AND A.NAME=SA.ARTIKEL "
				+ "AND S.NAME=SA.SHOP ";
		//System.out.println(Anfrage);
		try {
			return mgr.SendQuery(Anfrage, true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
				
	}


	public void SetUpdate(boolean _update){
		this.update = _update;
	}

    public void SetData(String[][] _data){
    	this.data = _data;
    }
    
    public void SetUIParam(JTable _tabelle){	
    	this.table = _tabelle;
    }
    
	public void SetParameter(DB2ConnectionManager _mgr,String _Region,String _Product, String _startDate,String _endDate,int _zeile, int _i){
    	this.mgr 		= _mgr;
    	 //mgr = new DB2ConnectionManager();
		this.Region 	= _Region;
    	this.Product 	= _Product;
    	this.startDate 	= _startDate;
    	this.endDate 	= _endDate;
    	this.zeile 		= _zeile;
    	this.i 			= _i;
    }
	
	@Override
	public void run() {
		
		try {
		
			if (update){
				table.setValueAt(Region, zeile,0);
				table.setValueAt(startDate + " - " + endDate, zeile,1);
			}else{
				data[zeile][0]= Region; 
				data[zeile][1]=startDate + " - " + endDate;
			}
			ResultSet r = MakeQuery(Region,Product,startDate,endDate);
			r.next();
			String Erg = r.getString(1);
			if(update){
				table.setValueAt(Erg, zeile, i);
			}else{
				data[zeile][i] =Erg;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
