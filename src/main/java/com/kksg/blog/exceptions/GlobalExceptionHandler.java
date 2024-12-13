package com.kksg.blog.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.kksg.blog.payloads.ApiResponse;
import com.kksg.blog.utils.AppConstants;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse> resouceNotFoundExceptionHandler(ResourceNotFoundException ex) {
		String message = ex.getMessage();
		ex.printStackTrace();
		ApiResponse apiResponse = new ApiResponse(AppConstants.FAILED, message, null, null);
		return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
		String string = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
		ex.printStackTrace();
		ApiResponse apiResponse = new ApiResponse(AppConstants.FAILED, string, null, null);
		return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ApiResponse> handleApiException(ApiException ex) {
		String message = ex.getMessage();
		ex.printStackTrace();
		ApiResponse apiResponse = new ApiResponse(AppConstants.FAILED, message, null, null);
		return new ResponseEntity<>(apiResponse, HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiResponse> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
		String message = "You can enter only Integer Value as Id";
		ex.printStackTrace();
		ApiResponse apiResponse = new ApiResponse(AppConstants.FAILED, message, null,null);
		return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
	}
	
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGlobalException(Exception ex) {
        ex.printStackTrace();
        ApiResponse apiResponse = new ApiResponse(AppConstants.FAILED, ex.getMessage(), null, null);
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR); 
    }
	
}
