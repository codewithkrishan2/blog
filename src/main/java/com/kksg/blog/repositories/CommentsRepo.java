package com.kksg.blog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kksg.blog.entities.Comments;

public interface CommentsRepo extends JpaRepository<Comments, Integer> {

}
