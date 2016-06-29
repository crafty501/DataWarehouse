package de.dis2016.model;

import java.sql.Date;

public class Purchase extends Contract{
	
	private int noOfInstallments;
	private int intrestRate;

	public Purchase() {}
	
	public Purchase(int contractno, Date date, String place, int noOfInstallments, int intrestRate){
		super(contractno, date, place);
		this.noOfInstallments = noOfInstallments;
		this.intrestRate = intrestRate;
	}
	
	public int getNoOfInstallments() {
		return noOfInstallments;
	}
	public int getIntrestRate() {
		return intrestRate;
	}
    
	public void setNoOfInstallments(int noOfInstallments) {
		this.noOfInstallments = noOfInstallments;
	}
	public void setIntrestRate(int intrestRate) {
		this.intrestRate = intrestRate;
	}
}
