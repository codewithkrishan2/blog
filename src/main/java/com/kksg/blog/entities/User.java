package com.kksg.blog.entities;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.kksg.blog.entities.enums.UserStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(
	    name = "user",
	    indexes = {
	        @Index(name = "idx_user_email", columnList = "email", unique = true), // Email is unique and frequently queried for login/authentication
	        @Index(name = "idx_user_status", columnList = "status"),              // Status may be queried for active/inactive users
	        @Index(name = "idx_user_created_on", columnList = "createdOn")        // CreatedOn can be used for sorting or filtering users
	    }
	)
public class User implements UserDetails {
	
    @Serial
    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(name = "user_name", nullable = false, length = 100)
    private String name;
    
    @Column(unique = true)
    private String email;
    
    private String password;
    
    private Integer otp;
    
    private LocalDateTime otpExpiration; 
    
    private String about;
    
    @Column(updatable = false)
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<Post> posts = new ArrayList<>();
    
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user", referencedColumnName = "userId"),
    			inverseJoinColumns = @JoinColumn(name = "role", referencedColumnName = "roleId"))
    private Set<Role> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private UserStatus status;
    
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<SimpleGrantedAuthority> authorities = 
				this.roles.stream()
				.map((role)-> new SimpleGrantedAuthority(role.getName()))
				.collect(Collectors.toList());
		return authorities;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
    @PrePersist
    public void onCreate() {
        createdOn = LocalDateTime.now();
        updatedOn = createdOn;
    }

    @PreUpdate
    public void onUpdate() {
        updatedOn = LocalDateTime.now();
    }
}
