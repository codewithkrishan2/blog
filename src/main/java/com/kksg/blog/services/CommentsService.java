package com.kksg.blog.services;

import com.kksg.blog.payloads.CommentsDto;

public interface CommentsService {

	CommentsDto createComment(CommentsDto commentsDto);
	void deleteComment(Integer commentId);
	void toggleLikeComment(Integer commentId, Integer userId);
	long getCommentLikeCount(Integer commentId);
}
