package de.ka.joplacli.sperrmullka.dto;

import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "spermuell")
public class Streets {

	@XmlElementWrapper(name = "streets")
	private Collection<Street> street;
	
	public Streets(){}

	public Collection<Street> getStreets() {
		return street;
	}

	public void setStreets(List<Street> street) {
		this.street = street;
	}
}
