package data;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import de.dis2016.model.Sales;

/**
 * Klasse zur Verwaltung aller Datenbank-Entitäten.
 * 
 * TODO: Aktuell werden alle Daten im Speicher gehalten. Ziel der Übung ist es,
 * schrittweise die Datenverwaltung in die Datenbank auszulagern. Wenn die
 * Arbeit erledigt ist, werden alle Sets dieser Klasse überflüssig.
 */
public class ImmoService {
	// Datensätze im Speicher

	
	private SessionFactory sessionFactory;

	public ImmoService() {
		sessionFactory = new Configuration().configure().buildSessionFactory();
	}
	
	
	
	private void deleteObject(Object o) {
		Transaction tx = null;
		Session session = sessionFactory.getCurrentSession();
		try {
			tx = session.beginTransaction();
			session.delete(o);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				try {
					tx.rollback();
				} catch (HibernateException e1) {
					System.err.println("Error rolling back transaction");
				}
				throw e;
			}
		}
	}

	private void addObjekt(Object o) {
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		session.save(o);
		session.getTransaction().commit();
	}

	private void updateObject(Object o) {
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		session.update(o);
		session.getTransaction().commit();
	}

	

	public void addContract(Sales sales) {
		addObjekt(sales);
	}
	
	
	
	
	
	
	public List<Sales> getSales() {
		Session session = sessionFactory.getCurrentSession();
		
		session.beginTransaction();
		@SuppressWarnings("unchecked")
		List<Sales> list = (List<Sales>) session.createCriteria(Sales.class).list();
		return list;
	}

	
	
}