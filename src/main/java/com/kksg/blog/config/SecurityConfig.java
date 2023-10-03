package com.kksg.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.kksg.blog.security.CustomUserDetailService;
import com.kksg.blog.security.JwtAuthenticationEntryPoint;
import com.kksg.blog.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableWebMvc
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Autowired
	private CustomUserDetailService customUserDetailService;
	
	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	
	@Autowired 
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	
	private static final String[] PUBLIC_URLS = {"/api/v1/auth/**", "/v3/api-docs", "/v2/api-docs",
            "/swagger-resources/**", "/swagger-ui/**", "/webjars/**"

    };

	private final String[] ADMIN_URLS = {
            "/user/**"
    };
	
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        
        http	
        		.csrf(csrf -> csrf.disable())
        		.authorizeHttpRequests(auth -> auth
        		.requestMatchers(PUBLIC_URLS).permitAll()
                .requestMatchers(HttpMethod.GET).permitAll()
                .requestMatchers(HttpMethod.DELETE, ADMIN_URLS).hasRole("ADMIN")
                /*.requestMatchers(HttpMethod.POST, ADMIN_URLS).hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, ADMIN_URLS).hasRole("ADMIN")*/
                .anyRequest()
                .authenticated())
        	.exceptionHandling(ex ->
    			ex.authenticationEntryPoint(this.jwtAuthenticationEntryPoint))
    		.sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    		;
        
       
       /*      
	    http.authorizeHttpRequests(
	            auth -> auth.requestMatchers("/signin", "/signup").permitAll()
	            .requestMatchers("/users/**", "/apps/**").hasAuthority("ADMIN")
	            .requestMatchers("/myapps/**").hasAuthority("CLIENT")
	            .anyRequest().authenticated()
	           )
	            .formLogin(formLogin -> formLogin
	                    .loginPage("/signin")
	                    .usernameParameter("email")
	                    .defaultSuccessUrl("/", true)
	                    .permitAll()
	            )
	            .rememberMe(rememberMe -> rememberMe.key("AbcdEfghIjkl..."))
	            .logout(logout -> logout.logoutUrl("/signout").permitAll());
	 
	    return http.build();
		
		*/
		
		
		
		http.addFilterBefore(this.jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		
		http.authenticationProvider(daoAuthenticationProvider());
		DefaultSecurityFilterChain build = http.build();
		
		return build;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration configuration) throws Exception {
		
		return configuration.getAuthenticationManager();
		
	}	
	
	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(this.customUserDetailService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}
}
