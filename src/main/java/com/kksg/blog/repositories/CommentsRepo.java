package com.kksg.blog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kksg.blog.entities.Comments;
import com.kksg.blog.entities.Post;
import com.kksg.blog.entities.User;

public interface CommentsRepo extends JpaRepository<Comments, Integer> {

	Long countByPost(Post post);

	Long countByUser(User user);

}
