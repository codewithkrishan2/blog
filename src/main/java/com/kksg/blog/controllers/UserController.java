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
import org.springframework.web.bind.annotation.RequestParam;
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

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody UserDto userDto) {
        try {
            UserDto createdUserDto = userService.createUser(userDto);
            ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, "User created successfully", createdUserDto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (ApiException ex) {
            ApiResponse response = new ApiResponse(AppConstants.FAILED, ex.getMessage(), null, null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(AppConstants.FAILED, "Error while creating user", null, null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<ApiResponse> updateUser(@Valid @RequestBody UserDto userDto, @PathVariable("userId") Integer uid) {
        try {
            UserDto updatedUserDto = userService.updateUser(userDto, uid);
            ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, "User updated successfully", updatedUserDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ApiException ex) {
            ApiResponse response = new ApiResponse(AppConstants.FAILED, ex.getMessage(), null, null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(AppConstants.FAILED, "Error while updating user", null, null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable("userId") Integer uid) {
        try {
            this.userService.deleteUser(uid);
            ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, "User deleted successfully", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ApiException ex) {
            ApiResponse response = new ApiResponse(AppConstants.FAILED, ex.getMessage(), null, null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(AppConstants.FAILED, "Error while deleting user", null, null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllUsers() {
        try {
            List<UserDto> allUsers = this.userService.getAllUsers();
            ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, "Users fetched successfully", allUsers);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ApiException ex) {
            ApiResponse response = new ApiResponse(AppConstants.FAILED, ex.getMessage(), null, null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(AppConstants.FAILED, "Error while fetching users", null, null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getOneUser(@PathVariable("userId") Integer uid) {
        try {
            UserDto userById = this.userService.getUserById(uid);
            ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, "User fetched successfully", userById);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            ApiResponse response = new ApiResponse(AppConstants.FAILED, ex.getMessage(), null, null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (ApiException ex) {
            ApiResponse response = new ApiResponse(AppConstants.FAILED, ex.getMessage(), null, null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(AppConstants.FAILED, "Something went wrong", null, null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/username")
    public ResponseEntity<ApiResponse> getUserByEmail(@RequestParam String email) {
    	try {
			UserDto userByEmail = this.userService.getUserByEmail(email);
			ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, "User is Present", userByEmail);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			ApiResponse response = new ApiResponse(AppConstants.FAILED, e.getMessage(), null, null);
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}
    }

}
