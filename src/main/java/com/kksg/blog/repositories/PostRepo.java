package com.kksg.blog.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kksg.blog.entities.Category;
import com.kksg.blog.entities.Post;
import com.kksg.blog.entities.User;

public interface PostRepo extends JpaRepository<Post, Integer> {

//	List<Post> findByUser(User user);
//	List<Post> findByPostCategory(Category postCategory);
//	List<Post> findByPostTitleContaining(String postTitle);

	Optional<Post> findBySlug(String slug);
	Boolean existsBySlug(String slug);
	Long countByUser(User user);
//	List<Post> findByTags_TagName(String tagName);

//	@Query("SELECT p FROM Post p WHERE p.postAddedDate >= :sevenDaysAgo ORDER BY p.likeCount DESC")
//	List<Post> findTopTrendingPosts(LocalDateTime sevenDaysAgo);

	@Query("SELECT p FROM Post p WHERE p.postAddedDate >= :sevenDaysAgo ORDER BY p.likeCount DESC")
	Page<Post> findTopTrendingPosts(LocalDateTime sevenDaysAgo, Pageable pageable);

	// Pagination support for fetching posts by user
	Page<Post> findByUser(User user, Pageable pageable);
	// Pagination support for fetching posts by category
	Page<Post> findByPostCategory(Category postCategory, Pageable pageable);
	// Pagination support for fetching posts by title (using partial match)
	Page<Post> findByPostTitleContaining(String postTitle, Pageable pageable);
	// Pagination support for fetching posts by tag name
	Page<Post> findByTags_TagName(String tagName, Pageable pageable);

}
