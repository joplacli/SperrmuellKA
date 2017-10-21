package de.ka.joplacli.sperrmullka.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "street")
public class Street {
	
	private String letter;
	private int streetId;
	private String streetName;
	private Date date;
	
	public Street(){}
	
	public int getStreetId() {
		return streetId;
	}
	public void setStreetId(int value) {
		this.streetId = value;
	}
	public String getStreetName() {
		return streetName;
	}
	public void setStreetName(String street) {
		this.streetName = street;
	}
	public String getLetter() {
		return letter;
	}
	public void setLetter(String string) {
		this.letter = string;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
