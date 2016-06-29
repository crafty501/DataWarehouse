import java.text.ParseException;

import data.CSV;
import data.DBService;

public class startup {
	
	
	public static void main(String[] args) throws ParseException {
		
		DBService dbService = new DBService();
		dbService.createShop();
		dbService.createArtikel();
		CSV csv = new CSV();
		csv.readFile();
	}
}
