package data;

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import de.dis2016.model.Artikel;
import de.dis2016.model.Shop;

public class DBService extends DB2ConnectionManager{
	
	private SessionFactory sessionFactory;
	
	public DBService() {
		super();
		sessionFactory = new Configuration().configure().buildSessionFactory();
    }
	
	public ResultSet SendQuery(String S, boolean result) throws SQLException {
		try {
			Statement stm = this.con.createStatement();
			if (result) {
				if (stm.execute(S)) {
					return stm.executeQuery(S);
				} else {
					return null;
				}
			} else {
				stm.execute(S);
				return null;
			}
		} catch (SQLException e) {
			throw e;
		}
    } 
	
	public void createShop() {
		String Anfrage = "SELECT DB2INST1.shopid.name, DB2INST1.stadtid.name, " +
			"DB2INST1.regionid.name, DB2INST1.landid.name FROM DB2INST1.Shopid " +
			"INNER JOIN DB2INST1.stadtid " +
			"ON DB2INST1.stadtid.stadtid = DB2INST1.Shopid.stadtid " +
			"INNER JOIN DB2INST1.regionid " +
			"ON DB2INST1.regionid.regionid = DB2INST1.stadtid.regionid " +
			"INNER JOIN DB2INST1.landid " +
			"ON DB2INST1.landid.landid = DB2INST1.regionid.landid";
		try {
			ResultSet result = this.SendQuery(Anfrage, true);
			if (result != null) {

				Session session = sessionFactory.getCurrentSession();
				session.beginTransaction();
				
				while (result.next()) {
					String name = result.getString(1);
					String stadt = result.getString(2);
					String region = result.getString(3);
					String land = result.getString(4);

					Shop shop = new Shop();
					shop.setName(name);
					shop.setStadt(stadt);
					shop.setRegion(region);
					shop.setLand(land);
					
					session.save(shop);
				}
				session.getTransaction().commit();
				System.out.println("Shop Tabelle wurde erstellt!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void createArtikel() {
	
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
			ResultSet result = this.SendQuery(Anfrage, true);
			if (result != null) {

				Session session = sessionFactory.getCurrentSession();
				session.beginTransaction();
				
				while (result.next()) {
					String name = result.getString(1);
					double preis = result.getDouble(2);
					String gruppe = result.getString(3);
					String familie = result.getString(4);
					String kategorie = result.getString(5);

					Artikel artikel = new Artikel(); 
					artikel.setName(name);
					artikel.setPreis(preis);
					artikel.setGruppe(gruppe);
					artikel.setFamilie(familie);
					artikel.setKategorie(kategorie);
					
					session.save(artikel);
				}
				session.getTransaction().commit();
				System.out.println("Artikel Tabelle wurde erstellt!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}	