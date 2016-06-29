package de.dis2016.model;


public abstract class Estate {
	private int id;
	private String city;
	private String postalCode;
	private String street;
	private String streetNr;
	private int squareArea;
	private String login;
	private int personid;
	private int contractnr;
	
	public Estate() {}
	
	public Estate(int id, String city, String postalCode, String street, String streetNr, int squareArea, String login, int personid, int contractnr) {
		this.id = id;
		this.city = city;
		this.postalCode = postalCode;
		this.street = street;
		this.streetNr = streetNr;
		this.squareArea = squareArea;
		this.login = login;
		this.personid = personid;
		this.contractnr = contractnr;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getStreetNr() {
		return streetNr;
	}
	public void setStreetNr(String streetNr) {
		this.streetNr = streetNr;
	}
	public int getSquareArea() {
		return squareArea;
	}
	public void setSquareArea(int squareArea) {
		this.squareArea = squareArea;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public int getPersonid() {
		return personid;
	}
	public void setPersonid(int personid) {
		this.personid = personid;
	}
	public int getContractnr() {
		return contractnr;
	}
	public void setContractnr(int contractnr) {
		this.contractnr = contractnr;
	}
	
	
}
