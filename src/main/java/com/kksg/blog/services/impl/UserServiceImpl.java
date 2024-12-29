 package com.kksg.blog.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kksg.blog.config.AppConstants;
import com.kksg.blog.entities.Role;
import com.kksg.blog.entities.User;
import com.kksg.blog.exceptions.ResourceNotFoundException;
import com.kksg.blog.payloads.UserDto;
import com.kksg.blog.repositories.PostRepo;
import com.kksg.blog.repositories.RoleRepo;
import com.kksg.blog.repositories.UserRepo;
import com.kksg.blog.services.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private RoleRepo roleRepo;
	
	@Autowired
	private PostRepo postRepo;
	
	@Override
	public UserDto createUser(UserDto userDto) {
		User user = this.dtoToUser(userDto);
		User savedUser = this.userRepo.save(user);
		return this.userToDtoUser(savedUser);
		
	}

	@Override
	public UserDto updateUser(UserDto userDto, Integer userId) {
		
		User user = this.userRepo.findById(userId)
				.orElseThrow(()-> new ResourceNotFoundException(
						"User", "Id", userId));
		user.setName(userDto.getName());
		user.setEmail(userDto.getEmail());
		user.setAbout(userDto.getAbout());
		user.setPassword(userDto.getPassword());
		User updatedUser = this.userRepo.save(user);
		return this.userToDtoUser(updatedUser);
	}

	@Override
	public UserDto getUserById(Integer userId) {
		User user = this.userRepo.findById(userId).orElseThrow(
				()-> new ResourceNotFoundException("User", "Id", userId));
		return this.userToDtoUser(user);
	}	

	@Override
	public List<UserDto> getAllUsers() {
		List<User> users = this.userRepo.findAll();
		return users.stream()
								.map(this::userToDtoUser)
								.collect(Collectors.toList());
	}

	//Delete use by user id
	@Override
	public void deleteUser(Integer userId) {
		User user = this.userRepo.findById(userId).orElseThrow(
					()-> new ResourceNotFoundException("User", " Id ", userId));
		user.getRoles().clear();
		postRepo.deleteAll(user.getPosts());
		//this.postRepo.deleteAll(user.getPosts());
		//userRepo.save(user);
		userRepo.delete(user);
	}
	

	public User dtoToUser(UserDto userDto) {
		return this.modelMapper.map(userDto, User.class);
		
	}

	public UserDto userToDtoUser(User user) {
		return this.modelMapper.map(user, UserDto.class);
	}

	@Override
	public UserDto registerNewUser(UserDto userDto) {
		
		User user = this.modelMapper.map(userDto, User.class);
		//encoding the password
		user.setPassword(this.passwordEncoder.encode(user.getPassword()));
		
		//setting the default role
		Role role = this.roleRepo.findById(AppConstants.NORLMAL_USER).get();
		user.getRoles().add(role);
		User savedNewUser = this.userRepo.save(user);
		return this.modelMapper.map(savedNewUser, UserDto.class);
	}



}
