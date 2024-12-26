package com.kksg.blog.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kksg.blog.entities.Category;
import com.kksg.blog.entities.Post;
import com.kksg.blog.entities.User;
import com.kksg.blog.entities.enums.PostStatus;

public interface PostRepo extends JpaRepository<Post, Integer> {

	Optional<Post> findBySlug(String slug);

	Boolean existsBySlug(String slug);

	Long countByUser(User user);

	@Query("SELECT p FROM Post p " + "WHERE p.updatedOn >= :fifteenDaysAgo "
			+ "ORDER BY (p.likeCount + p.viewCount + size(p.comments)) DESC")
	Page<Post> findTopTrendingPosts(LocalDateTime fifteenDaysAgo, Pageable pageable);

	@Query("SELECT p FROM Post p " + "LEFT JOIN p.tags t " + "WHERE p.postTitle LIKE %:keyword% OR "
			+ "p.postContent LIKE %:keyword% OR " + "p.user.name LIKE %:keyword% OR "
			+ "p.postCategory.categoryTitle LIKE %:keyword% OR " + "t.tagName LIKE %:keyword% OR "
			+ "p.metaTitle LIKE %:keyword% OR " + "p.metaDescription LIKE %:keyword% OR "
			+ "p.metaKeywords LIKE %:keyword% OR " + "p.slug LIKE %:keyword%")
	Page<Post> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

	Page<Post> findByUser(User user, Pageable pageable);

	Page<Post> findByPostCategory(Category postCategory, Pageable pageable);

	Page<Post> findByPostTitleContaining(String postTitle, Pageable pageable);

	Page<Post> findByTags_TagName(String tagName, Pageable pageable);

	Page<Post> findByUser_UserIdAndStatus(Integer userId, PostStatus status, Pageable pageable);

}
