import java.text.ParseException;

import data.CSV;
import data.DBService;

public class startup {
	
	
	public static void main(String[] args) throws ParseException {
		
		DBService dbService = new DBService();
		//TODO Tabellen rein laden
		//Yannicks part
		CSV csv = new CSV(dbService);
		csv.readFile();
	}
}
