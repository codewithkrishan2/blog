package com.kksg.blog.exceptions;

import java.io.Serial;

public class ApiException extends RuntimeException {


    /**
     * 
     */
    @Serial
    private static final long serialVersionUID = 1L;

	public ApiException() {
		super();
	}

	public ApiException(String message) {
		super(message);
		
	}

}	
