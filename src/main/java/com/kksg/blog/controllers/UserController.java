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

import com.kksg.blog.payloads.ApiResponse;
import com.kksg.blog.payloads.UserDto;
import com.kksg.blog.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
    private UserService userService;
    
    //Create a new User

    @PostMapping("/create")
    public ResponseEntity<UserDto> createUser( @Valid
    		@RequestBody UserDto userDto) {
       
    	UserDto createdUserDto = userService.createUser(userDto);
        return new ResponseEntity<>(createdUserDto, HttpStatus.CREATED);
    }

    //update an existing User
    
    @PutMapping("/update/{userId}")
    public ResponseEntity<UserDto> updatUser(
    		@RequestBody UserDto userDto, 
    		@PathVariable("userId") Integer uid)
    {
    	UserDto updatedUserDto = this.userService.updateUser(userDto, uid);
    	return new ResponseEntity<>(updatedUserDto, HttpStatus.OK);
    	

    }

    //delete an existing User
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable("userId") Integer uid) {
		this.userService.deleteUser(uid);
    	return new ResponseEntity<ApiResponse>( new ApiResponse("User Deleted", true) , HttpStatus.OK);
	}
    //get all Users
    
    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers() {
    	List<UserDto> allUsers = this.userService.getAllUsers();
		return new ResponseEntity<>(allUsers, HttpStatus.OK);
	}
    
    //get a single User by ID
    @GetMapping("/one/{userId}")
    public ResponseEntity<UserDto> getOneUser(@PathVariable("userId") Integer uid) {
    	UserDto userById = this.userService.getUserById(uid);
		return new ResponseEntity<>(userById, HttpStatus.OK);
	}

}
