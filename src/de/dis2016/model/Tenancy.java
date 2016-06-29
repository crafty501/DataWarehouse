package de.dis2016.model;

import java.sql.Date;

public class Tenancy extends Contract{
	
	private Date startDate;
	private int duration;
	private int additionalCosts;
	
	public Tenancy() {}
	
	public Tenancy(int contractno, Date date, String place, Date startDate, int duration, int additionalCosts){
		super(contractno, date, place);
		this.startDate = startDate;
		this.duration = duration;
		this.additionalCosts = additionalCosts;
	}
	public Date getStartDate() {
		return startDate;
	}
	public int getDuration() {
		return duration;
	}
	public int getAdditionalCosts() {
		return additionalCosts;
	}
    
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public void setAdditionalCosts(int additionalCosts) {
		this.additionalCosts = additionalCosts;
	}
	
}
