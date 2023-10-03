package com.kksg.blog;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.kksg.blog.config.AppConstants;
import com.kksg.blog.entities.Role;
import com.kksg.blog.repositories.RoleRepo;


@SpringBootApplication
public class BlogApiApplication implements CommandLineRunner{

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private RoleRepo roleRepo;
	
	public static void main(String[] args) {
		SpringApplication.run(BlogApiApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper(); 
	}

	@Override
	public void run(String... args) throws Exception {
		//System.out.println(passwordEncoder.encode("123456"));
		
		try {
			Role role1 = new Role();
			role1.setRoleId(AppConstants.ADMIN_USER);
			role1.setName("ROLE_ADMIN");
			
			Role role2 = new Role();
			role2.setRoleId(AppConstants.NORLMAL_USER);
			role2.setName("ROLE_NORMAL");
			
			List<Role> roles = List.of(role1,role2);
			List<Role> savedRoles = this.roleRepo.saveAll(roles);
			savedRoles.forEach(r->{
				System.out.println(r.getName());
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
