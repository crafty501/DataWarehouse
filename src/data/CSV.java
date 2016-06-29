package data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Diese Klasse soll die csv-datei lesen können 
 * und die notwendigen INSERT Queriesfür jede Zeile erstellen.
 * Bei dieser Erstellung werden Datentypen on the fly konvertiert(falls notwendig)
 * 
 * @author callya
 *
 */
public class CSV {

	
	
	public CSV(){
		
	}
	
	/**
	 * Diese Methode erstellt 
	 */
	public void ReadLines(){
		
		String line;
		try {
		    InputStream fis = new FileInputStream("data/sales.csv");
		    InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
		    BufferedReader br = new BufferedReader(isr);
		
		    while ((line = br.readLine()) != null){
		       
		    	System.out.println(line);
		    }
		 }catch (IOException e ){
			System.out.println("Error in CSV-Class"+e.getMessage());
		 }
		
	}
}
