package com.kksg.blog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kksg.blog.entities.Category;

public interface CategoryRepo extends JpaRepository<Category, Integer> {

	public Category findByCategoryTitle(String categoryTitle);
}
