package com.kksg.blog.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kksg.blog.entities.Category;
import com.kksg.blog.entities.Post;
import com.kksg.blog.entities.User;

public interface PostRepo extends JpaRepository<Post, Integer> {

	List<Post> findByUser(User user);
	List<Post> findByPostCategory(Category postCategory);
	List<Post> findByPostTitleContaining(String postTitle);
	Optional<Post> findBySlug(String slug);
	Boolean existsBySlug(String slug);
}
