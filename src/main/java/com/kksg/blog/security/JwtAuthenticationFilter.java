package com.kksg.blog.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private UserDetailsService userDetailsService;

	// Autowire the JwtTokenHelper to create a token for a user
	
	@Autowired
	private JwtTokenHelper jwtTokenHelper;

	@Override
	protected void doFilterInternal(
			HttpServletRequest request, 
			HttpServletResponse response, 
			FilterChain filterChain)
			throws ServletException, IOException {

		// 1. Get Token

		String requestToken = request.getHeader("Authorization");

		// request Token will look like this : Bearer
		// dsbfcdhnj38273.23480ksjd.2348923982ndkhf
		System.out.println("from JwtAuthenticationFilter.class The Token is "+requestToken);

		String username = null;
		String token = null;

		// Check if the request token starts with Bearer
		if (requestToken != null && requestToken.startsWith("Bearer ")) {

			// Remove the Bearer from the request token
			token = requestToken.substring(7);

			// Attempt to get the username from the JWT token
			try {
				username = this.jwtTokenHelper.getUsernameFromToken(token);
			} catch (IllegalArgumentException e) {
				// If the token is invalid, print a message
				System.out.println("Unable to get Jwt token");
			} catch (ExpiredJwtException e) {
				// If the token has expired, print a message
				System.out.println("Jwt Token has Expired");
			} catch (MalformedJwtException e) {
				// If the token is invalid, print a message
				System.out.println("Invalid Jwt token");
			}

		} else {
			// If the request token does not begin with Bearer, print a message
			System.out.println("Jwt Token does not begin with Bearer or invalid header value : sysout");
		}
		// Once we have got the token , now we will validate the token

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			// Get the user details from the database
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

			// Validate the token
			if (this.jwtTokenHelper.validateToken(token, userDetails)) {
				// Every thing is Okay till here
				// Now We have to Authenticate
				// Create a new instance of UsernamePasswordAuthenticationToken
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				// Set the details of the authentication token
				usernamePasswordAuthenticationToken
						.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				// Set the authentication token to the security context holder
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			} else {
				System.out.println("Invalid Jwt token");
			}

		} else {
			System.out.println("Username is null or context is not null");
		}

		// Call the next filter
		filterChain.doFilter(request, response);

	}

}
