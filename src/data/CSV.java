package data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * Diese Klasse soll die csv-datei lesen können 
 * und die notwendigen INSERT Queriesfür jede Zeile erstellen.
 * Bei dieser Erstellung werden Datentypen on the fly konvertiert(falls notwendig)
 * 
 * @author callya
 *
 */
public class CSV extends DB2ConnectionManager{

	String anfrage = "INSERT INTO table (DATUM,LADEN) VALUES (?,?)";
	
	
	public CSV(){
		
	}
	
	/**
	 * Diese Methode erstellt 
	 * @throws  
	 */
	public void ReadLines(){
		
		try {
			PreparedStatement pstmt = con.prepareStatement(anfrage);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		String line;
		try {
		    InputStream fis = new FileInputStream("data/sales.csv");
		    InputStreamReader isr = new InputStreamReader(fis, Charset.forName("ISO-8859-1"));
		    BufferedReader br = new BufferedReader(isr);
		
		    while ((line = br.readLine()) != null){
		       
		    	String[] zeile = line.split(";");
		    	
		    	String date 	= zeile[0];
		    	String store	= zeile[1];
		    	String thing    = zeile[2];
		    	String verkauft = zeile[3];
		    	String umsatz   = zeile[4];
		    	
		    	
		    	String Query = "INSERT INTO table (DATUM,LADEN) VALUES ('"+date+"')";
		    	
		    }
		 }catch (IOException e ){
			System.out.println("Error in CSV-Class"+e.getMessage());
		 }
		
	}
}
