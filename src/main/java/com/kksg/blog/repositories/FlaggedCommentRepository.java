package com.kksg.blog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kksg.blog.entities.FlaggedComment;

public interface FlaggedCommentRepository extends JpaRepository<FlaggedComment, Long> {

}
