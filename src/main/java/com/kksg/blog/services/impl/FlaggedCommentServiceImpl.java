package com.kksg.blog.services.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kksg.blog.entities.Comments;
import com.kksg.blog.entities.FlaggedComment;
import com.kksg.blog.entities.User;
import com.kksg.blog.exceptions.ResourceNotFoundException;
import com.kksg.blog.repositories.CommentsRepo;
import com.kksg.blog.repositories.FlaggedCommentRepository;
import com.kksg.blog.repositories.UserRepo;
import com.kksg.blog.services.FlaggedCommentService;
import com.kksg.blog.services.NotificationService;

@Service
public class FlaggedCommentServiceImpl implements FlaggedCommentService {

	@Autowired
    private FlaggedCommentRepository flaggedCommentRepository;

    @Autowired
    private NotificationService emailNotificationService;

    @Autowired
    private CommentsRepo commentsRepository;

    @Autowired
    private UserRepo userRepository;

    // Flag a comment
    @Override
    public FlaggedComment flagComment(Integer commentId, Integer userId, String reason) {
        Comments comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "Comment Id", commentId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "User Id", userId));

        FlaggedComment flaggedComment = new FlaggedComment();
        flaggedComment.setComment(comment);
        flaggedComment.setUser(user);
        flaggedComment.setReason(reason);
        flaggedComment.setFlaggedAt(LocalDateTime.now());

        // Save the flagged comment
        FlaggedComment savedFlaggedComment = flaggedCommentRepository.save(flaggedComment);
        // Notify admin
        emailNotificationService.sendFlaggedCommentNotification(savedFlaggedComment);
        return savedFlaggedComment;
    }
}
