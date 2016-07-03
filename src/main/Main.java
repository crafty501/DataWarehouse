package main;
import java.text.ParseException;

import analiser.AnaliserJFrame;
import data.DB2ConnectionManager;
import data.WarehouseService;
import kolja.query.uitabelle;

public class Main {
	
	
	public static void main(String[] args) throws ParseException {
		
		DB2ConnectionManager mgr = new DB2ConnectionManager();
		
		//new WarehouseService(mgr);
		
		
		//Uitabelle ui = new Uitabelle(mgr);
		//ui.setVisible(true);
		//System.out.println("ui gestartet");
		new AnaliserJFrame(mgr);
	}
}
