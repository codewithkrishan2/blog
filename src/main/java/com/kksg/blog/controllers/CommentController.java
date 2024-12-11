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
            // Call service to create a comment
            CommentsDto createdComment = this.commentsService.createComment(commentsDto);
            // Prepare successful response with created comment data
            ApiResponse response = new ApiResponse("Success", null, "Comment created successfully", createdComment);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            // Handle error and return failure response
            ApiResponse response = new ApiResponse("Failed", "Error while creating comment", "Unable to create comment", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Reply to an existing comment
    @PostMapping("/comment/{parentCommentId}/reply")
    public ResponseEntity<ApiResponse> replyToComment(@PathVariable Integer parentCommentId, @RequestBody CommentsDto dto) {
        try {
            // Call service to reply to a comment using parentCommentId
            CommentsDto replyComment = commentsService.replyToComment(parentCommentId, dto);
            // Prepare successful response with reply comment data
            ApiResponse response = new ApiResponse("Success", null, "Comment replied successfully", replyComment);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            // Handle error and return failure response
            ApiResponse response = new ApiResponse("Failed", "Error while replying to comment", "Unable to reply to comment", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Delete a comment
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<ApiResponse> deleteComment(@PathVariable Integer commentId) {
        try {
            // Call service to delete the comment by commentId
            this.commentsService.deleteComment(commentId);
            // Prepare success response indicating deletion
            ApiResponse response = new ApiResponse("Success", null, "Comment deleted successfully", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // Handle error and return failure response
            ApiResponse response = new ApiResponse("Failed", "Error while deleting comment", "Unable to delete comment", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Toggle like for a comment
    @PostMapping("/comment/{commentId}/like")
    public ResponseEntity<ApiResponse> toggleLikeComment(@PathVariable Integer commentId, @RequestParam Integer userId) {
        try {
            // Call service to toggle the like status of the comment
            commentsService.toggleLikeComment(commentId, userId);
            // Prepare success response indicating that like status was toggled
            ApiResponse response = new ApiResponse("Success", null, "Like toggled successfully", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // Handle error and return failure response
            ApiResponse response = new ApiResponse("Failed", "Error while toggling like", "Unable to toggle like for the comment", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Get the like count of a comment
    @GetMapping("/comment/{commentId}/like-count")
    public ResponseEntity<ApiResponse> getCommentLikeCount(@PathVariable Integer commentId) {
        try {
            // Call service to get the like count of the comment
            long likeCount = commentsService.getCommentLikeCount(commentId);
            // Prepare response with the like count
            ApiResponse response = new ApiResponse("Success", null, "Like count fetched successfully", likeCount);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // Handle error and return failure response
            ApiResponse response = new ApiResponse("Failed", "Error while fetching like count", "Unable to fetch like count for the comment", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Flag a comment
    @PostMapping("/comment/{commentId}/flag")
    public ResponseEntity<ApiResponse> flagComment(@PathVariable Integer commentId, @RequestBody FlagRequest flagRequest) {
        try {
            // Call service to flag the comment with the reason
            flaggedCommentService.flagComment(commentId, flagRequest.getUserId(), flagRequest.getReason());
            // Prepare success response indicating that the comment was flagged
            ApiResponse response = new ApiResponse("Success", null, "Comment flagged successfully", null);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            // Handle error and return failure response
            ApiResponse response = new ApiResponse("Failed", "Error while flagging comment", "Unable to flag comment", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
