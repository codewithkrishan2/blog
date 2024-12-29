package com.kksg.blog.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.kksg.blog.payloads.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	// Get the message from the ResourceNotFoundException
	// Create a new ApiResponse object with the message and false as the response
	// Return a new ResponseEntity with the ApiResponse and the HttpStatus.NOT_FOUND
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse> resouceNotFoundExceptionHandler( ResourceNotFoundException ex) {
		
		String message = ex.getMessage();
		ApiResponse apiResponse = new ApiResponse(message, false);
		return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> methodArgumentNotValidException( MethodArgumentNotValidException ex){
		
		Map<String, String> resp = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error)->{
			String fieldName = ((FieldError) error).getField();
			String message = error.getDefaultMessage();
			resp.put(fieldName, message);
		});
		
		return new ResponseEntity<Map<String,String>>(resp, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ApiResponse> handleApiException( ApiException ex) {
		
		String message = ex.getMessage();
		ApiResponse apiResponse = new ApiResponse(message, true);
		return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiResponse> methodArgumentTypeMismatchException( MethodArgumentTypeMismatchException ex) {
		
		String message = "You can enter only Integer Value as Id in url";
		ApiResponse apiResponse = new ApiResponse(message, false);
		return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
	}
	
}
