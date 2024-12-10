package com.kksg.blog.payloads;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserDto {

    private int userId;
    
    @NotBlank(message = "Name is mandatory")
    @Size(min = 3, message = "Name Must be min 3 char")
    private String name;
    
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email Address is not valid")
    private String email;
    
    
    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, max = 16, message = "password must be min - 6 and max -16 chars")
    private String password;
    
    @NotBlank(message = "About is mandatory")
    private String about;
    
    private Set<RolesDto> roles = new HashSet<>();
    
    private String createdOn;
    
    private String updatedOn;
    
    @JsonIgnore
    public String getPassword() {
    	return this.password;
    }
    
    @JsonProperty
    public void setPassword(String password) {
	    this.password = password;
    }
} 
