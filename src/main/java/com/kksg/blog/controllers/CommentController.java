package com.kksg.blog.controllers;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.kksg.blog.services.CommentsService;

@RestController
@RequestMapping("/api/v1")
public class CommentController {

	@Autowired
	private CommentsService commentsService;
	
	@PostMapping("/comment")
	public ResponseEntity<CommentsDto> createComment( @RequestBody CommentsDto commentsDto) {
		CommentsDto createdComment = this.commentsService.createComment(commentsDto);
		return new ResponseEntity<CommentsDto>(createdComment, HttpStatus.CREATED);
	}
	
	@DeleteMapping("/comment/{commentId}")
	public ResponseEntity<ApiResponse> deleteComment(
			@PathVariable Integer commentId) {
		this.commentsService.deleteComment(commentId);
		return new ResponseEntity<ApiResponse>(new ApiResponse("Comments Deleted Successfully", true), HttpStatus.OK);
	}
	
	// Toggle like for a comment
    @PostMapping("/comment/{commentId}/like")
    public ResponseEntity<Void> toggleLikeComment(@PathVariable Integer commentId, @RequestParam Integer userId) {
        commentsService.toggleLikeComment(commentId, userId);
        return ResponseEntity.ok().build();  // Return 200 OK after toggling
    }
    
    //get like count
	@GetMapping("/comment/{commentId}/like-count")
	public ResponseEntity<Long> getCommentLikeCount(@PathVariable Integer commentId) {
		long likeCount = commentsService.getCommentLikeCount(commentId);
		return ResponseEntity.ok(likeCount);  // Return 200 OK with the like count
	}
}
