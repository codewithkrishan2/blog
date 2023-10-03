package com.kksg.blog.services;

import com.kksg.blog.payloads.CommentsDto;

public interface CommentsService {

	CommentsDto createComment(CommentsDto commentsDto, Integer postId);
	void deleteComment(Integer commentId);
	
}
