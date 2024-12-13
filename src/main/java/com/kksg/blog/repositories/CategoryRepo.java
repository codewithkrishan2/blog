package com.kksg.blog.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kksg.blog.entities.Category;

public interface CategoryRepo extends JpaRepository<Category, Integer> {

	Optional<Category> findByCategoryTitle(String categoryTitle);
}
