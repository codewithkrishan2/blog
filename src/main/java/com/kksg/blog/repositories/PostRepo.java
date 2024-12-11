package com.kksg.blog.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kksg.blog.entities.Category;
import com.kksg.blog.entities.Post;
import com.kksg.blog.entities.User;

public interface PostRepo extends JpaRepository<Post, Integer> {

	List<Post> findByUser(User user);
	List<Post> findByPostCategory(Category postCategory);
	List<Post> findByPostTitleContaining(String postTitle);
	Optional<Post> findBySlug(String slug);
	Boolean existsBySlug(String slug);
	Long countByUser(User user);
	
	 List<Post> findByTags_TagName(String tagName);
	
    @Query("SELECT p FROM Post p WHERE p.postAddedDate >= :sevenDaysAgo ORDER BY p.likeCount DESC")
	List<Post> findTopTrendingPosts(LocalDateTime sevenDaysAgo);
}
