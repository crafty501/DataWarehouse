package data;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import de.dis2016.model.Artikel;
import de.dis2016.model.Sales;
import de.dis2016.model.Shop;

/**
 * Klasse zur Verwaltung aller Datenbank-Entitäten.
 * 
 * TODO: Aktuell werden alle Daten im Speicher gehalten. Ziel der Übung ist es,
 * schrittweise die Datenverwaltung in die Datenbank auszulagern. Wenn die
 * Arbeit erledigt ist, werden alle Sets dieser Klasse überflüssig.
 */
public class DBService{
	// Datensätze im Speicher

	
	private SessionFactory sessionFactory;

	public DBService() {
		sessionFactory = new Configuration().configure().buildSessionFactory();
	}
	
	private void addObjekt(Object o) {
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		session.save(o);
		session.getTransaction().commit();
	}

	public void begin() {
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
	}
	
	
	public void addSales(Sales sales) {
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
	}
	
	public void addArtikel(Artikel artikel) {
		addObjekt(artikel);
	}
	
	public void addShop(Shop shop) {
		addObjekt(shop);
	}

	public List<Sales> getSales() {
		Session session = sessionFactory.getCurrentSession();	
		session.beginTransaction();
		@SuppressWarnings("unchecked")
		List<Sales> list = (List<Sales>) session.createCriteria(Sales.class).list();
		return list;
	}
	
}