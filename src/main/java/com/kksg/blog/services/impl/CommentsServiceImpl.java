package com.kksg.blog.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kksg.blog.entities.Comments;
import com.kksg.blog.entities.Post;
import com.kksg.blog.exceptions.ResourceNotFoundException;
import com.kksg.blog.payloads.CommentsDto;
import com.kksg.blog.repositories.CommentsRepo;
import com.kksg.blog.repositories.PostRepo;
import com.kksg.blog.services.CommentsService;

@Service
public class CommentsServiceImpl implements CommentsService {

	@Autowired
	private PostRepo postRepo;
	
	@Autowired
	private CommentsRepo commentsRepo;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	public CommentsDto createComment(CommentsDto commentsDto, Integer postId) {
		
		Post post = this.postRepo.findById(postId).orElseThrow(()-> new ResourceNotFoundException("Post", "Post Id ", postId));
		
		Comments comment = this.modelMapper.map(commentsDto, Comments.class);
		
		comment.setPost(post);
		
		Comments savedComment = this.commentsRepo.save(comment);
		
		return this.modelMapper.map(savedComment, CommentsDto.class);	
	}

	@Override
	public void deleteComment(Integer commentId) {
		Comments comment = this.commentsRepo.findById(commentId).orElseThrow(()-> new ResourceNotFoundException("Comment", "Comment Id", commentId));
		this.commentsRepo.delete(comment);
		

	}

}
