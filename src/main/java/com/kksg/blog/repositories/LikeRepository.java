package com.kksg.blog.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kksg.blog.entities.Comments;
import com.kksg.blog.entities.Likes;
import com.kksg.blog.entities.Post;
import com.kksg.blog.entities.User;

public interface LikeRepository extends JpaRepository<Likes, Long> {

	// Find a like by post and user
    Optional<Likes> findByPostAndUser(Post post, User user);
    // Find a like by comment and user
    Optional<Likes> findByCommentAndUser(Comments comment, User user);
    // Count the likes for a post
    Integer countByPost(Post post);   
    // Count the likes for a comment
    Integer countByComment(Comments comment);
	Long countByUser(User user);
	
}
