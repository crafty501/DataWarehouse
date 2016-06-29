package de.dis2016.model;

import java.sql.Date;

public abstract class Contract {
	private int id;
	private Date date;
	private String place;
	
	public Contract() {}
	
	public Contract(int id, Date date, String place){
		this.id = id;
		this.date = date;
		this.place = place;
	}
	
	public int getId() {
		return id;
	}
	public Date getDate() {
		return date;
	}
	public String getPlace() {
		return place;
	}
    
	public void setId(int id) {
		this.id = id;
	}
	public void setDate(Date date) {
		this.date = date ;
	}
	public void setPlace(String place ) {
		this.place = place;
	}
	
}
