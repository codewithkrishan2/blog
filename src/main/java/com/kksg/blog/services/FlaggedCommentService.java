package com.kksg.blog.services;

import com.kksg.blog.entities.FlaggedComment;

public interface FlaggedCommentService {

	FlaggedComment flagComment(Integer commentId, Integer userId, String reason);

}
