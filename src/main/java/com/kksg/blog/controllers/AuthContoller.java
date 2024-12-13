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
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.kksg.blog.utils.AppConstants;

import jakarta.validation.Valid;

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

	// password encoder
	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostMapping("/login")
	public ResponseEntity<ApiResponse> createToken(@RequestBody JwtAuthRequest request) throws Exception {

		User user = this.userRepo.findByEmail(request.getUsername())
				.orElseThrow(() -> new ApiException("User not found"));
		this.authenticate(request.getUsername(), request.getPassword());
		Integer otp = 123456;
		// String encryptedOtp = EncryptionUtil.encrypt(String.valueOf(otp));

		System.out.println("-------------------------------" + otp);
		String hashedOtp = passwordEncoder.encode(otp.toString());

		System.out.println("---------------------------------" + hashedOtp);
		user.setOtp(hashedOtp);
		user.setOtpExpiration(LocalDateTime.now().plusMinutes(10)); // Set OTP expiration to 5 minutes

		userRepo.save(user);

		// Send OTP to user's email
		this.emailService.sendOtpEmail(user.getEmail(), otp);

		// Inform the user that OTP has been sent
		ApiResponse apiResponse = new ApiResponse(AppConstants.SUCCESS, null, "OTP sent successfully", null);
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);

	}

	@PostMapping("/verify-otp")
	public ResponseEntity<ApiResponse> verifyOtp(@RequestBody OtpRequest otpRequest) throws Exception {
		// Find the user by email
		User user = this.userRepo.findByEmail(otpRequest.getUsername())
				.orElseThrow(() -> new ApiException("User not found"));

		// Check if the OTP is valid
		Integer userOtp = otpRequest.getOtp();

		System.out.println("-------------------------------" + userOtp);
		boolean matches = passwordEncoder.matches(userOtp.toString(), user.getOtp());

		if (!matches) {
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
		user.setOtp(null);
		user.setOtpExpiration(null);
		userRepo.save(user);

		// Create and return JWT response
		JwtAuthResponse response = new JwtAuthResponse();
		response.setToken(token);
		response.setUser(mapper.map(user, UserDto.class));

		// Return success response
		ApiResponse apiResponse = new ApiResponse(AppConstants.SUCCESS, null, "OPT Varification Successful", response);
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);

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
	public ResponseEntity<ApiResponse> registerNewUser(@Valid @RequestBody UserDto userDto) {
		UserDto registeredUser = this.userService.registerNewUser(userDto);
		ApiResponse apiResponse = new ApiResponse("SUCCESS", null, "User Registerd Successfully", registeredUser);
		return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
	}

	// get loggedin user data
	@GetMapping("/current-user")
	public ResponseEntity<ApiResponse> getUser(Principal principal) {
		User user = this.userRepo.findByEmail(principal.getName()).get();
		if (user == null) {
			return new ResponseEntity<>(new ApiResponse(AppConstants.FAILED, "User not found", null, null), HttpStatus.OK);
		}
		UserDto userDto = this.mapper.map(user, UserDto.class);
		ApiResponse apiResponse = new ApiResponse(AppConstants.SUCCESS, null, "User fetched successfully", userDto);
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}
}
