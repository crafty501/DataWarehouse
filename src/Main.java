import java.text.ParseException;

import data.CSV;
import data.DB2ConnectionManager;
import data.DBService;
import kolja.query.uitabelle;

public class Main {
	
	
	public static void main(String[] args) throws ParseException {
		
		/*
		DBService dbService = new DBService();
		dbService.createShop();
		dbService.createArtikel();
		CSV csv = new CSV();
		csv.readFile();
		*/
		
		
		DB2ConnectionManager mgr = new DB2ConnectionManager();
		uitabelle ui = new uitabelle(mgr);
		ui.setVisible(true);
		System.out.println("ui gestartet");
	}
}
