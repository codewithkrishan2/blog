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
import com.kksg.blog.utils.AppConstants;

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
		CommentsDto createdComment = this.commentsService.createComment(commentsDto);
		ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, "Commented successfully", createdComment);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// Reply to an existing comment
	@PostMapping("/comment/reply/{parentCommentId}")
	public ResponseEntity<ApiResponse> replyToComment(@PathVariable Integer parentCommentId,
			@RequestBody CommentsDto dto) {
		CommentsDto replyComment = commentsService.replyToComment(parentCommentId, dto);
		ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, "Replied successfully", replyComment);
		return new ResponseEntity<>(response, HttpStatus.CREATED);

	}

	// Delete a comment
	@DeleteMapping("/comment/{commentId}")
	public ResponseEntity<ApiResponse> deleteComment(@PathVariable Integer commentId) {
		this.commentsService.deleteComment(commentId);
		ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, "Deleted successfully", null);
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@PostMapping("/comment/like/{commentId}")
	public ResponseEntity<ApiResponse> toggleLikeComment(@PathVariable Integer commentId,
			@RequestParam Integer userId) {
		commentsService.toggleLikeComment(commentId, userId);
		ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, "Liked", null);
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	// Get the like count of a comment
	@GetMapping("/comment/like-count/{commentId}")
	public ResponseEntity<ApiResponse> getCommentLikeCount(@PathVariable Integer commentId) {
		long likeCount = commentsService.getCommentLikeCount(commentId);
		ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, "Successfully fetched like count", likeCount);
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	// Flag a comment
	@PostMapping("/comment/flag/{commentId}")
	public ResponseEntity<ApiResponse> flagComment(@PathVariable Integer commentId,
			@RequestBody FlagRequest flagRequest) {
		flaggedCommentService.flagComment(commentId, flagRequest.getUserId(), flagRequest.getReason());
		ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, "flagged", null);
		return new ResponseEntity<>(response, HttpStatus.CREATED);

	}
}
