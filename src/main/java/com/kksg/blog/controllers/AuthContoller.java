package com.kksg.blog.controllers;

import java.security.Principal;
import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kksg.blog.entities.User;
import com.kksg.blog.exceptions.ApiException;
import com.kksg.blog.payloads.ApiResponse;
import com.kksg.blog.payloads.JwtAuthRequest;
import com.kksg.blog.payloads.JwtAuthResponse;
import com.kksg.blog.payloads.OtpRequest;
import com.kksg.blog.payloads.UserDto;
import com.kksg.blog.repositories.UserRepo;
import com.kksg.blog.security.JwtTokenHelper;
import com.kksg.blog.services.EmailService;
import com.kksg.blog.services.UserService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthContoller {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenHelper jwtTokenHelper;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepo userRepo;
	@Autowired
	private ModelMapper mapper;
	
	@Autowired
	private EmailService emailService;

	@PostMapping("/login")
	public ResponseEntity<ApiResponse> createToken(@RequestBody JwtAuthRequest request) throws Exception {

		try {
			User user = this.userRepo.findByEmail(request.getUsername()).orElseThrow(() -> new ApiException("User not found"));
			this.authenticate(request.getUsername(), request.getPassword());
			//UserDetails userDetails = this.userDetailsService.loadUserByUsername(request.getUsername());
			
			
			
			// Generate OTP and set expiration
//		    int otp = this.emailService.generateOtp();
			int otp = 123456;
		    System.out.println(otp);
		    user.setOtp(otp);
		    user.setOtpExpiration(LocalDateTime.now().plusMinutes(5)); // Set OTP expiration to 5 minutes

			
		    // Save OTP and expiration to the database
		    userRepo.save(user);

		    // Send OTP to user's email
		    this.emailService.sendOtpEmail(user.getEmail(), otp);
		    
		    // Inform the user that OTP has been sent
		    return new ResponseEntity<ApiResponse>(new ApiResponse("OTP sent successfully", true), HttpStatus.OK);

		} catch (Exception e) {
			throw new ApiException("Invalid Username or password");
		}
		
//		String generatedToken = this.jwtTokenHelper.generateToken(userDetails);
//		
//		JwtAuthResponse response = new JwtAuthResponse();
//		response.setToken(generatedToken);
//		response.setUser(mapper.map((User) userDetails, UserDto.class));
//		return new ResponseEntity<JwtAuthResponse>(response, HttpStatus.OK);
	}
	
	
	@PostMapping("/verify-otp")
	public ResponseEntity<JwtAuthResponse> verifyOtp(@RequestBody OtpRequest otpRequest) throws Exception {
	    // Find the user by email
	    User user = this.userRepo.findByEmail(otpRequest.getUsername()).orElseThrow(() -> new ApiException("User not found"));

	    // Check if the OTP is valid
	    if (user.getOtp() != otpRequest.getOtp()) {
	        throw new ApiException("Invalid OTP");
	    }

	    // Check if the OTP has expired
	    if (user.getOtpExpiration().isBefore(LocalDateTime.now())) {
	        throw new ApiException("OTP has expired");
	    }

	    // OTP is valid, proceed to generate JWT token
	    UserDetails userDetails = this.userDetailsService.loadUserByUsername(otpRequest.getUsername());
	    String token = this.jwtTokenHelper.generateToken(userDetails);

	    // Clear OTP from user record (optional, for security)
	    user.setOtp(0);
	    user.setOtpExpiration(null);
	    userRepo.save(user);

	    // Create and return JWT response
	    JwtAuthResponse response = new JwtAuthResponse();
	    response.setToken(token);
	    response.setUser(mapper.map(user, UserDto.class));
	    
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}

	
	
	
	

	private void authenticate(String username, String password) throws Exception {

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
				password);
		try {
			this.authenticationManager.authenticate(authenticationToken);
		} catch (BadCredentialsException e) {
			System.out.println("Controller method excp: Invalid details");
			throw new ApiException("Invalid Username or password");
		}
	}

	// Register New User API

	@PostMapping("/register")
	public ResponseEntity<UserDto> registerNewUser(@RequestBody UserDto userDto) {
		try {
			UserDto registeredUser = this.userService.registerNewUser(userDto);
			return new ResponseEntity<UserDto>(registeredUser, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<UserDto>(HttpStatus.BAD_REQUEST);
		}
	}

	// get loggedin user data

	@GetMapping("/current-user/")
	public ResponseEntity<UserDto> getUser(Principal principal) {
		User user = this.userRepo.findByEmail(principal.getName()).get();
		return new ResponseEntity<UserDto>(this.mapper.map(user, UserDto.class), HttpStatus.OK);
	}
}
