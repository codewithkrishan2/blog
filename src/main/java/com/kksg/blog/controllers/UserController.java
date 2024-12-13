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
		UserDto createdUserDto = userService.createUser(userDto);
		ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, "User created successfully", createdUserDto);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PutMapping("/update/{userId}")
	public ResponseEntity<ApiResponse> updateUser(@Valid @RequestBody UserDto userDto,
			@PathVariable("userId") Integer uid) {
		UserDto updatedUserDto = userService.updateUser(userDto, uid);
		ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, "User updated successfully", updatedUserDto);
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
	@DeleteMapping("/delete/{userId}")
	public ResponseEntity<ApiResponse> deleteUser(@PathVariable("userId") Integer uid) {
		this.userService.deleteUser(uid);
		ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, "User deleted successfully", null);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/all")
	public ResponseEntity<ApiResponse> getAllUsers() {
		List<UserDto> allUsers = this.userService.getAllUsers();
		ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, "Users fetched successfully", allUsers);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponse> getOneUser(@PathVariable("userId") Integer uid) {
		UserDto userById = this.userService.getUserById(uid);
		ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, "User fetched successfully", userById);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/username")
	public ResponseEntity<ApiResponse> getUserByEmail(@RequestParam String email) {
		UserDto userByEmail = this.userService.getUserByEmail(email);
		ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, "User is Present", userByEmail);
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

}
