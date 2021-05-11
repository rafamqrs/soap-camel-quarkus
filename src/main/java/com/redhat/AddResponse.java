package com.redhat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class AddResponse {

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
