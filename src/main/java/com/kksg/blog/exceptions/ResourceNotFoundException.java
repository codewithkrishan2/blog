package com.kksg.blog.exceptions;

import lombok.Getter;
import lombok.Setter;

//@SuppressWarnings("serial")
@Setter
@Getter
public class ResourceNotFoundException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	//Declare a String resourceName and String fieldsName and long fieldValue
	String resourceName;
	String fieldsName;
	long fieldValue;
	
	//Create a constructor that takes in a String resourceName, String fieldsName and long fieldValue
	public ResourceNotFoundException(String resourceName, String fieldsName, long fieldValue) {
		//Call the superclass constructor with a message that includes the resourceName, fieldsName and fieldValue
		super(String.format("%s not found with %s : %s", resourceName,fieldsName,fieldValue));
		
		//Set the instance variables resourceName, fieldsName and fieldValue
		this.resourceName = resourceName;
		this.fieldsName = fieldsName;
		this.fieldValue = fieldValue;
	}
	
	
}