package com.kksg.blog.exceptions;

public class ApiException extends RuntimeException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ApiException() {
		super();
	}

	public ApiException(String message) {
		super(message);
		
	}

}
