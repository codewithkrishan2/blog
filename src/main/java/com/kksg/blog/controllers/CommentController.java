package com.kksg.blog.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kksg.blog.payloads.ApiResponse;
import com.kksg.blog.payloads.CommentsDto;
import com.kksg.blog.payloads.FlagRequest;
import com.kksg.blog.services.CommentsService;
import com.kksg.blog.services.FlaggedCommentService;

@RestController
@RequestMapping("/api/v1")
public class CommentController {


    private CommentsService commentsService;
    private FlaggedCommentService flaggedCommentService;

    public CommentController(CommentsService commentsService, FlaggedCommentService flaggedCommentService) {
        this.commentsService = commentsService;
        this.flaggedCommentService = flaggedCommentService;
    }

    // Create a new comment
    @PostMapping("/comment")
    public ResponseEntity<ApiResponse> createComment(@RequestBody CommentsDto commentsDto) {
        try {
            CommentsDto createdComment = this.commentsService.createComment(commentsDto);
            ApiResponse response = new ApiResponse("Success", null, createdComment);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("Failed", "Error while creating comment", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Reply to an existing comment
    @PostMapping("/comment/{parentCommentId}/reply")
    public ResponseEntity<ApiResponse> replyToComment(@PathVariable Integer parentCommentId, @RequestBody CommentsDto dto) {
        try {
            CommentsDto replyComment = commentsService.replyToComment(parentCommentId, dto);
            ApiResponse response = new ApiResponse("Success", null, replyComment);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("Failed", "Error while replying to comment", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Delete a comment
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<ApiResponse> deleteComment(@PathVariable Integer commentId) {
        try {
            this.commentsService.deleteComment(commentId);
            ApiResponse response = new ApiResponse("Success", "Comment deleted successfully", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("Failed", "Error while deleting comment", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Toggle like for a comment
    @PostMapping("/comment/{commentId}/like")
    public ResponseEntity<ApiResponse> toggleLikeComment(@PathVariable Integer commentId, @RequestParam Integer userId) {
        try {
            commentsService.toggleLikeComment(commentId, userId);
            ApiResponse response = new ApiResponse("Success", "Like toggled successfully", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("Failed", "Error while toggling like", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Get the like count of a comment
    @GetMapping("/comment/{commentId}/like-count")
    public ResponseEntity<ApiResponse> getCommentLikeCount(@PathVariable Integer commentId) {
        try {
            long likeCount = commentsService.getCommentLikeCount(commentId);
            ApiResponse response = new ApiResponse("Success", null, likeCount);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("Failed", "Error while fetching like count", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Flag a comment
    @PostMapping("/comment/{commentId}/flag")
    public ResponseEntity<ApiResponse> flagComment(@PathVariable Integer commentId, @RequestBody FlagRequest flagRequest) {
        try {
            // Flag the comment and notify the admin
            flaggedCommentService.flagComment(commentId, flagRequest.getUserId(), flagRequest.getReason());
            ApiResponse response = new ApiResponse("Success", "Comment flagged successfully", null);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("Failed", "Error while flagging comment", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
	
}
