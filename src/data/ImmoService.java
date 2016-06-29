package data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;

import de.dis2016.model.Apartment;
import de.dis2016.model.Contract;
import de.dis2016.model.Estate;
import de.dis2016.model.House;
import de.dis2016.model.Makler;
import de.dis2016.model.Person;
import de.dis2016.model.Purchase;
import de.dis2016.model.Tenancy;

/**
 * Klasse zur Verwaltung aller Datenbank-Entitäten.
 * 
 * TODO: Aktuell werden alle Daten im Speicher gehalten. Ziel der Übung ist es,
 * schrittweise die Datenverwaltung in die Datenbank auszulagern. Wenn die
 * Arbeit erledigt ist, werden alle Sets dieser Klasse überflüssig.
 */
public class ImmoService implements IDB2 {
	// Datensätze im Speicher

	private SessionFactory sessionFactory;

	public ImmoService() {
		sessionFactory = new Configuration().configure().buildSessionFactory();
	}

	@Override
	public void addContract(Contract contract) {
		addObjekt(contract);
	}

	@Override
	public List<Contract> getContracts() {
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		@SuppressWarnings("unchecked")
		List<Contract> list = (List<Contract>) session.createCriteria(Contract.class).list();
		return list;
	}

	@Override
	public void updateMakler(Makler makler) {
		updateObject(makler);
	}

	@Override
	public void addMakler(Makler makler) {
		this.addObjekt(makler);
	}

	@Override
	public List<Makler> getMarklers() {
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		@SuppressWarnings("unchecked")
		List<Makler> list = (List<Makler>) session.createCriteria(Makler.class).list();
		return list;
	}

	@Override
	public Makler getMakler(String login) {
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		Makler makler = (Makler) session.get(Makler.class, login);
		return makler;
	}

	@Override
	public List<Estate> getEstates(String login) {
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		@SuppressWarnings("unchecked")
		List<Estate> list = (List<Estate>) session.createCriteria(Estate.class).list();
		return list;
		/*
		List<Estate> estates = new ArrayList<>();
		estates.addAll(getAllEstates(House.class, login));
		estates.addAll(getAllEstates(Apartment.class, login));
		return estates;*/
	}

	@Override
	public List<Estate> getHouses(String login) {
		return null;
	}

	@Override
	public List<Estate> getApartments(String login) {
		return null;
	}
	
	private List <Estate> getAllEstates (Class cla, String login) {
		List<Estate> liste = new ArrayList<>();
		Transaction tx = null;
		Session session = sessionFactory.getCurrentSession();
		try {
			
			tx = session.beginTransaction();
			liste.addAll(session.createCriteria(cla).add(Restrictions.like("login", login)).list());
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
		return liste;
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

	@Override
	public void deleteEstate(Estate estate) {
		deleteObject(estate);
	}

	@Override
	public void addApartment(Apartment apartment) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addHouse(House house) {
		// TODO Auto-generated method stub

	}

	@Override
	public int addEstate(Estate estate) throws SQLException {
		int contractNumber 	= estate.getContractnr();
		int personID 		= estate.getPersonid();  
		
		if (isgueltigerVertrag(contractNumber)){
			if(isgueltigePerson(personID)){
					addObjekt(estate);
			}else{
				SQLException e1 = new SQLException("Keine g�ltige Personnummer");
				throw e1;
			}
		}else{
			SQLException e2 = new SQLException("Keine g�ltige Vertragsnummer");
			throw e2;
		}
		//addObjekt(estate);
		
		// unused
		return 0;
	}

	@Override
	public void updateEstate(Estate estate) {
		updateObject(estate);
	}

	@Override
	public void updateApartment(Apartment estate) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateHouse(House estate) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPerson(Person person) {
		addObjekt(person);
	}

	@Override
	public void updatePerson(Person person) {
		updateObject(person);
	}

	@Override
	public List<Person> getPersons() {
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		@SuppressWarnings("unchecked")
		List<Person> list = (List<Person>) session.createCriteria(Person.class).list();
		return list;
	}

	@Override
	public Person getPerson(int id) {
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		Person person = (Person) session.get(Person.class, id);
		return person;
	}
public boolean isgueltigerVertrag(int id){
		
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		Contract contract = (Contract) session.get(Contract.class, id);
		
		if(contract == null){
			System.out.println("Contact number"+id+" false");
			return false;
		}else{
			System.out.println("Contact number"+id+" true");
			return true;
		}
	
	}
	
public boolean isgueltigePerson(int id){
		
		Person p = this.getPerson(id);
		
		if(p == null){
			return false;
		}else{
			return true;
		}
	
	}
}
