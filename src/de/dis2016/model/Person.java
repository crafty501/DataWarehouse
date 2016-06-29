package de.dis2016.model;

/**
CREATE TABLE Person(
    ID          integer NOT NULL GENERATED ALWAYS AS IDENTITY,
    First_Name    varchar(255),
    Name          varchar(255),
    Adress        varchar(999),
    PRIMARY KEY(ID)
);
 */
public class Person {

	private int id = -1;
	private String firstName;
	private String name;
	private String adress;
	
	public Person() {}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getAdress() {
		return adress;
	}
	public void setAdress(String adress) {
		this.adress = adress;
	}
	

}
