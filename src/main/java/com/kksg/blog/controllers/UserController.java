package com.kksg.blog.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kksg.blog.exceptions.ApiException;
import com.kksg.blog.exceptions.ResourceNotFoundException;
import com.kksg.blog.payloads.ApiResponse;
import com.kksg.blog.payloads.UserDto;
import com.kksg.blog.services.UserService;
import com.kksg.blog.utils.AppConstants;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

	@Autowired
	private UserService userService;

	// Create a new User
	@PostMapping("/create")
	public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody UserDto userDto) {
		try {
			UserDto createdUserDto = userService.createUser(userDto);
			ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, createdUserDto);
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (ApiException ex) {
			// Return failure response in case of exception
			ApiResponse apiResponse = new ApiResponse("FAILED", ex.getMessage(), null);
			return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			ApiResponse response = new ApiResponse("Failed", "Error while creating user", null);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
	}

	// Update an existing User
	@PutMapping("/update/{userId}")
	public ResponseEntity<ApiResponse> updateUser(@RequestBody UserDto userDto, @PathVariable("userId") Integer uid) {
		try {
			UserDto updatedUserDto = this.userService.updateUser(userDto, uid);
			ApiResponse response = new ApiResponse("Success", null, updatedUserDto);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (ApiException ex) {
			// Return failure response in case of exception
			ApiResponse apiResponse = new ApiResponse("FAILED", ex.getMessage(), null);
			return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			ApiResponse response = new ApiResponse("Failed", "Error while updating user", null);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// Delete an existing User
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/delete/{userId}")
	public ResponseEntity<ApiResponse> deleteUser(@PathVariable("userId") Integer uid) {
		try {
			this.userService.deleteUser(uid);
			ApiResponse response = new ApiResponse("Success", "User deleted successfully", null);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (ApiException ex) {
			// Return failure response in case of exception
			ApiResponse apiResponse = new ApiResponse("FAILED", ex.getMessage(), null);
			return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			ApiResponse response = new ApiResponse("Failed", "Error while deleting user", null);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// Get all Users
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/all")
	public ResponseEntity<ApiResponse> getAllUsers() {
		try {
			List<UserDto> allUsers = this.userService.getAllUsers();
			ApiResponse response = new ApiResponse("Success", null, allUsers);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (ApiException ex) {
			// Return failure response in case of exception
			ApiResponse apiResponse = new ApiResponse("FAILED", ex.getMessage(), null);
			return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			ApiResponse response = new ApiResponse("Failed", "Error while fetching users", null);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// Get a single User by ID
	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponse> getOneUser(@PathVariable("userId") Integer uid) {
		try {
			UserDto userById = this.userService.getUserById(uid);
			ApiResponse response = new ApiResponse("Success", null, userById);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (ResourceNotFoundException ex) {
			ApiResponse response = new ApiResponse("Failed", ex.getMessage(), null);
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		} catch (ApiException ex) {
			// Return failure response in case of exception
			ApiResponse apiResponse = new ApiResponse("FAILED", ex.getMessage(), null);
			return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			ApiResponse response = new ApiResponse("Failed", "Something went wrong", null);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
