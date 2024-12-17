package com.kksg.blog.services.impl;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kksg.blog.entities.Role;
import com.kksg.blog.entities.User;
import com.kksg.blog.exceptions.ApiException;
import com.kksg.blog.exceptions.ResourceNotFoundException;
import com.kksg.blog.payloads.UserDto;
import com.kksg.blog.repositories.PostRepo;
import com.kksg.blog.repositories.RoleRepo;
import com.kksg.blog.repositories.UserRepo;
import com.kksg.blog.services.UserService;
import com.kksg.blog.utils.AppConstants;

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
	@CachePut(value = "users", key = "#userDto.userId")
	public UserDto createUser(UserDto userDto) {
		User user = this.dtoToUser(userDto);

		Optional<User> byEmail = this.userRepo.findByEmail(user.getEmail());
		if (byEmail.isPresent()) {
	        throw new ApiException("Email already exists");
	    }

		User savedUser = this.userRepo.save(user);
		return this.userToDtoUser(savedUser);

	}

	@Override
	public UserDto updateUser(UserDto userDto, Integer userId) {
		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));
		user.setName(userDto.getName());
		user.setEmail(userDto.getEmail());
		user.setAbout(userDto.getAbout());
		user.setPassword(userDto.getPassword());
		User updatedUser = this.userRepo.save(user);
		UserDto userToDtoUserUpdated = this.userToDtoUser(updatedUser);
		return userToDtoUserUpdated;
	}

	@Override
	@Cacheable(value = "users", key = "#userId") 
	public UserDto getUserById(Integer userId) {
		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));
		return this.userToDtoUser(user);
	}

	@Override
	public List<UserDto> getAllUsers() {
		List<User> users = this.userRepo.findAll();
		List<UserDto> userDtos = users.stream().map(user -> this.userToDtoUser(user)).collect(Collectors.toList());
		return userDtos;
	}

	@Override
	@CacheEvict(value = "users", key = "#userId")
	public void deleteUser(Integer userId) {
		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", " Id ", userId));
		user.getRoles().clear();
		postRepo.deleteAll(user.getPosts());
		userRepo.delete(user);
	}

	public User dtoToUser(UserDto userDto) {
		User user = this.modelMapper.map(userDto, User.class);
		return user;

	}

	// user to dto we are converting it manually
	public UserDto userToDtoUser(User user) {
		UserDto userDto = this.modelMapper.map(user, UserDto.class);
		return userDto;
	}

	@Override
	public UserDto registerNewUser(UserDto userDto) {

		User user = this.modelMapper.map(userDto, User.class);
		
		userRepo.findByEmail(userDto.getEmail()).ifPresent(u -> {
			throw new ApiException("Email already exists");
		});

		// encoding the password
		user.setPassword(this.passwordEncoder.encode(user.getPassword()));

		// setting the default role
		Role role = this.roleRepo.findById(AppConstants.NORLMAL_USER).get();

		user.getRoles().add(role);

		User savedNewUser = this.userRepo.save(user);
		UserDto userDtoSaved = this.modelMapper.map(savedNewUser, UserDto.class);
		return userDtoSaved;
	}

	@Override
	public String getRoleOfLoggedInUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		List<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
		return String.join(", ", roles);
	}

	
	//get user by email
	@Override
	@Cacheable(value = "users", key = "#email")
	public UserDto getUserByEmail(String email) {
		User user = this.userRepo.findByEmail(email).orElseThrow(() -> new ApiException("User not present"));
		return this.modelMapper.map(user, UserDto.class);
	}
}
