package de.dis2016.model;

import java.sql.Date;

public class Sales{

	private Artikel artikel;
	private Shop shop;
	private Date datum;
	private int verkauft;
	private double umsatz;
	private int id;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public Sales() {}
	
	public Artikel getArtikel() {
		return artikel;
	}
	public void setArtikel(Artikel artikel) {
		this.artikel = artikel;
	}
	public Shop getShop() {
		return shop;
	}
	public void setShop(Shop shop) {
		this.shop = shop;
	}
	public Date getDatum() {
		return datum;
	}
	public void setDatum(Date datum) {
		this.datum = datum;
	}
	public int getVerkauft() {
		return verkauft;
	}
	public void setVerkauft(int verkauft) {
		this.verkauft = verkauft;
	}
	public double getUmsatz() {
		return umsatz;
	}
	public void setUmsatz(double umsatz) {
		this.umsatz = umsatz;
	}
			
	
	
}
