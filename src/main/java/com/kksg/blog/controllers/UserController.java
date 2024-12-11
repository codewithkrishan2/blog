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
            // Create the user via the service layer
            UserDto createdUserDto = userService.createUser(userDto);
            // Successful response with the created user data
            ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, "User created successfully", createdUserDto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (ApiException ex) {
            // Return failure response in case of API-specific exception
            ApiResponse response = new ApiResponse(AppConstants.FAILED, ex.getMessage(), null, null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Generic failure response
            ApiResponse response = new ApiResponse(AppConstants.FAILED, "Error while creating user", null, null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Update an existing User
    @PutMapping("/update/{userId}")
    public ResponseEntity<ApiResponse> updateUser(@Valid @RequestBody UserDto userDto, @PathVariable("userId") Integer uid) {
        try {
            // Update the user via the service layer
            UserDto updatedUserDto = userService.updateUser(userDto, uid);
            // Successful response with the updated user data
            ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, "User updated successfully", updatedUserDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ApiException ex) {
            // Return failure response in case of API-specific exception
            ApiResponse response = new ApiResponse(AppConstants.FAILED, ex.getMessage(), null, null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Generic failure response
            ApiResponse response = new ApiResponse(AppConstants.FAILED, "Error while updating user", null, null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete an existing User
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable("userId") Integer uid) {
        try {
            // Delete the user via the service layer
            this.userService.deleteUser(uid);
            // Successful response indicating deletion
            ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, "User deleted successfully", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ApiException ex) {
            // Return failure response in case of API-specific exception
            ApiResponse response = new ApiResponse(AppConstants.FAILED, ex.getMessage(), null, null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Generic failure response
            ApiResponse response = new ApiResponse(AppConstants.FAILED, "Error while deleting user", null, null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get all Users
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllUsers() {
        try {
            // Get all users from the service
            List<UserDto> allUsers = this.userService.getAllUsers();
            // Successful response with list of users
            ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, "Users fetched successfully", allUsers);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ApiException ex) {
            // Return failure response in case of API-specific exception
            ApiResponse response = new ApiResponse(AppConstants.FAILED, ex.getMessage(), null, null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Generic failure response
            ApiResponse response = new ApiResponse(AppConstants.FAILED, "Error while fetching users", null, null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get a single User by ID
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getOneUser(@PathVariable("userId") Integer uid) {
        try {
            // Get the user by ID from the service
            UserDto userById = this.userService.getUserById(uid);
            // Successful response with the user data
            ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, "User fetched successfully", userById);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            // Return failure response when the resource is not found
            ApiResponse response = new ApiResponse(AppConstants.FAILED, ex.getMessage(), null, null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (ApiException ex) {
            // Return failure response in case of API-specific exception
            ApiResponse response = new ApiResponse(AppConstants.FAILED, ex.getMessage(), null, null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Generic failure response
            ApiResponse response = new ApiResponse(AppConstants.FAILED, "Something went wrong", null, null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
