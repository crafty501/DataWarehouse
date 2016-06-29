package data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Locale;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import de.dis2016.model.Sales;

/**
 * Diese Klasse soll die csv-datei lesen können 
 * und die notwendigen INSERT Queriesfür jede Zeile erstellen.
 * Bei dieser Erstellung werden Datentypen on the fly konvertiert(falls notwendig)
 * 
 * @author callya
 *
 */
public class CSV{
	
	private SimpleDateFormat dateFormat;
	private NumberFormat numberFormat;
	private SessionFactory sessionFactory;
	private BufferedReader br;
	
	public CSV(){
		dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		numberFormat = NumberFormat.getInstance(Locale.FRANCE);
		sessionFactory = new Configuration().configure().buildSessionFactory();
	}
	
	/**
	 * @throws ParseException 
	 * Diese Methode erstellt 
	 * @throws  
	 */
	public void readFile() throws ParseException{
		
		String line;
		try {
		    InputStream fis = new FileInputStream("data/sales.csv");
		    InputStreamReader isr = new InputStreamReader(fis, Charset.forName("ISO-8859-1"));
		    br = new BufferedReader(isr);
		
		    br.readLine();
			Session session = sessionFactory.getCurrentSession();
			session.beginTransaction();
		    int i = 0;
		    while ((line = br.readLine()) != null){
		    	String[] zeile = line.split(";");
		    	
		    	if(zeile.length != 5){
		    		continue;
		    	}
		    	
		    	Sales sale = new Sales();
		    	
		    	Date date 	= dateFormat.parse(zeile[0]);
		        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		    	
		    	String shop	= zeile[1];
		    	String artikel = zeile[2];
		    	int verkauft = Integer.parseInt(zeile[3]);
		    	String umsatz = zeile[4];
		    	
		    	sale.setArtikel(artikel);
		    	sale.setDatum(sqlDate);
		    	sale.setShop(shop);
		        
		        sale.setUmsatz(numberFormat.parse(umsatz).doubleValue());
		    	sale.setVerkauft(verkauft);
		    	
		    	session.save(sale);
		    	i++;
		    	if ( i % 2000 == 0 ) {
		    	    session.flush();
		    	    session.clear();
		    	}
		    }
		    session.getTransaction().commit();
		 }catch (IOException e ){
			System.out.println("Error in CSV-Class"+e.getMessage());
		 }
		 System.out.println("CSV einlesen fertig!");
	}
}
