package com.kksg.blog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kksg.blog.entities.Role;

public interface RoleRepo extends JpaRepository<Role, Integer> {

	
}
