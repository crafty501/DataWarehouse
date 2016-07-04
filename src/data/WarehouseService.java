package data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Locale;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import de.dis2016.model.Artikel;
import de.dis2016.model.Sales;
import de.dis2016.model.Shop;

/**
 * Diese Klasse soll die csv-datei lesen können 
 * und die notwendigen INSERT Queriesfür jede Zeile erstellen.
 * Bei dieser Erstellung werden Datentypen on the fly konvertiert(falls notwendig)
 * 
 * @author callya
 *
 */
public class WarehouseService{
	
	private SimpleDateFormat dateFormat;
	private NumberFormat numberFormat;
	private SessionFactory sessionFactory;
	private BufferedReader br;
	
	DB2ConnectionManager mgr;
	
	public WarehouseService(DB2ConnectionManager mgr) throws ParseException{
		sessionFactory = new Configuration().configure().buildSessionFactory();
		
		this.mgr = new DB2ConnectionManager();;
		dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		numberFormat = NumberFormat.getInstance(Locale.FRANCE);
		System.out.println("before factory");
		System.out.println("tabllen angelegt");
		fillShop();
		fillArtikel();
		fillSales();
	}
	
	/**
	 * @throws ParseException 
	 * Diese Methode erstellt 
	 * @throws  
	 */
	public void fillSales() throws ParseException{
		
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
		 System.out.println("Sales Tabelle wurde gefuellt!");
	}
	
	
	
	public void fillShop() {
		String query = "SELECT DB2INST1.shopid.name, DB2INST1.stadtid.name, " +
			"DB2INST1.regionid.name, DB2INST1.landid.name FROM DB2INST1.Shopid " +
			"INNER JOIN DB2INST1.stadtid " +
			"ON DB2INST1.stadtid.stadtid = DB2INST1.Shopid.stadtid " +
			"INNER JOIN DB2INST1.regionid " +
			"ON DB2INST1.regionid.regionid = DB2INST1.stadtid.regionid " +
			"INNER JOIN DB2INST1.landid " +
			"ON DB2INST1.landid.landid = DB2INST1.regionid.landid";
		try {
			ResultSet rs = mgr.sendQuery(query, true);
			if (rs != null) {

				Session session = sessionFactory.getCurrentSession();
				session.beginTransaction();
				
				while (rs.next()) {
					String name = rs.getString(1);
					String stadt = rs.getString(2);
					String region = rs.getString(3);
					String land = rs.getString(4);

					Shop shop = new Shop();
					shop.setName(name);
					shop.setStadt(stadt);
					shop.setRegion(region);
					shop.setLand(land);
					
					session.save(shop);
				}
				session.getTransaction().commit();
				System.out.println("Shop Tabelle wurde gefuellt!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void fillArtikel() {
	
		String Anfrage = "SELECT DB2INST1.articleid.name, DB2INST1.articleid.preis, " + 
			"DB2INST1.productgroupid.name, DB2INST1.productfamilyid.name, " +
			"DB2INST1.productcategoryid.name FROM DB2INST1.articleid " +
			"INNER JOIN DB2INST1.productgroupid " +
			"ON DB2INST1.productgroupid.productgroupid = DB2INST1.articleid.productgroupid " +
			"INNER JOIN DB2INST1.productfamilyid " +
			"ON DB2INST1.productfamilyid.productfamilyid = DB2INST1.productgroupid.productfamilyid " +
			"INNER JOIN DB2INST1.productcategoryid " +
			"ON DB2INST1.productcategoryid.productcategoryid = DB2INST1.productfamilyid.productcategoryid";
		try {
			ResultSet rs = mgr.sendQuery(Anfrage, true);
			if (rs != null) {

				Session session = sessionFactory.getCurrentSession();
				session.beginTransaction();
				
				while (rs.next()) {
					String name = rs.getString(1);
					double preis = rs.getDouble(2);
					String gruppe = rs.getString(3);
					String familie = rs.getString(4);
					String kategorie = rs.getString(5);

					Artikel artikel = new Artikel(); 
					artikel.setName(name);
					artikel.setPreis(preis);
					artikel.setGruppe(gruppe);
					artikel.setFamilie(familie);
					artikel.setKategorie(kategorie);
					
					session.save(artikel);
				}
				session.getTransaction().commit();
				System.out.println("Artikel Tabelle wurde gefuellt!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
