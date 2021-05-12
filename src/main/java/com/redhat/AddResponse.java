package com.redhat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

//@XmlRootElement(name = "AddResponse")
//@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddResponse {
	
	@JacksonXmlProperty(localName="AddResult")
	protected int addResult;

	/**
	 * Gets the value of the addResult property.
	 * 
	 */
	public int getAddResult() {
		return addResult;
	}

	/**
	 * Sets the value of the addResult property.
	 * 
	 */
	public void setAddResult(int value) {
		this.addResult = value;
	}

}
