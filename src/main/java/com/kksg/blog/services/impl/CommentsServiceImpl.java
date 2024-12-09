package com.kksg.blog.services.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kksg.blog.entities.Comments;
import com.kksg.blog.entities.Like;
import com.kksg.blog.entities.Post;
import com.kksg.blog.entities.User;
import com.kksg.blog.exceptions.ResourceNotFoundException;
import com.kksg.blog.payloads.CommentsDto;
import com.kksg.blog.repositories.CommentsRepo;
import com.kksg.blog.repositories.LikeRepository;
import com.kksg.blog.repositories.PostRepo;
import com.kksg.blog.repositories.UserRepo;
import com.kksg.blog.services.CommentsService;

@Service
public class CommentsServiceImpl implements CommentsService {

	@Autowired
	private PostRepo postRepo;
	
	@Autowired
	private CommentsRepo commentsRepo;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private LikeRepository likeRepo;
	
	@Override
	public CommentsDto createComment(CommentsDto commentsDto) {
		Integer postId = commentsDto.getPostId();
		Post post = this.postRepo.findById(postId).orElseThrow(()-> new ResourceNotFoundException("Post", "Post Id ", postId));
		User user = userRepo.findById(commentsDto.getUserId()).orElseThrow(()-> new ResourceNotFoundException("User", "User Id ", commentsDto.getUserId()));
		Comments comment = this.modelMapper.map(commentsDto, Comments.class);
		comment.setPost(post);
		comment.setUser(user);
		comment.setUsername(user.getName());
		comment.setCreatedAt(LocalDateTime.now());
		return this.modelMapper.map(this.commentsRepo.save(comment), CommentsDto.class);	
	}

	@Override
	public void deleteComment(Integer commentId) {
		Comments comment = this.commentsRepo.findById(commentId).orElseThrow(()-> new ResourceNotFoundException("Comment", "Comment Id", commentId));
		this.commentsRepo.delete(comment);
	}

	@Override
	public void toggleLikeComment(Integer commentId, Integer userId) {
	    // Fetch the comment and user from the repository
	    Comments comment = commentsRepo.findById(commentId)
	            .orElseThrow(() -> new ResourceNotFoundException("Comment", "Comment Id", commentId));
	    User user = userRepo.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User", "User Id", userId));

	    // Check if the user has already liked the comment
	    Optional<Like> existingLike = likeRepo.findByCommentAndUser(comment, user);

	    if (existingLike.isPresent()) {
	        // If the like exists, remove the like (unlike)
	        likeRepo.delete(existingLike.get());
	    } else {
	        // If the like does not exist, create a new like (like the comment)
	        Like newLike = new Like();
	        newLike.setComment(comment);
	        newLike.setUser(user);
	        newLike.setLikeDate(LocalDateTime.now());

	        // Save the new like to the repository
	        likeRepo.save(newLike);
	    }
	}

	@Override
	public long getCommentLikeCount(Integer commentId) {
	    Comments comment = commentsRepo.findById(commentId)
	            .orElseThrow(() -> new ResourceNotFoundException("Comment", "Comment Id", commentId));
	    return likeRepo.countByComment(comment);  // Return the like count for the comment
	}
	
}
