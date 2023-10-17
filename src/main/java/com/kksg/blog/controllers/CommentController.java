package com.kksg.blog.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kksg.blog.payloads.ApiResponse;
import com.kksg.blog.payloads.CommentsDto;
import com.kksg.blog.services.CommentsService;

@RestController
@RequestMapping("/api/v1")
public class CommentController {

	@Autowired
	private CommentsService commentsService;
	
	@PostMapping("/post/{postId}/comment")
	public ResponseEntity<CommentsDto> createComment(
			@RequestBody CommentsDto commentsDto,
			@PathVariable Integer postId) {
		
		CommentsDto createdComment = this.commentsService.createComment(commentsDto, postId);
		return new ResponseEntity<CommentsDto>(createdComment, HttpStatus.CREATED);
	}
	
	@DeleteMapping("/comment/{commentId}")
	public ResponseEntity<ApiResponse> deleteComment(
			@PathVariable Integer commentId) {
		this.commentsService.deleteComment(commentId);
		return new ResponseEntity<ApiResponse>(new ApiResponse("Comments Deleted Successfully", true), HttpStatus.OK);
	}
}
