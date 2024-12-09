package com.kksg.blog.services;

import java.util.List;

import com.kksg.blog.payloads.PostDto;
import com.kksg.blog.payloads.PostResponse;

public interface PostService {

	//Create a post
	PostDto createPost(PostDto postDto, Integer userId, Integer categoryId);
	
	//Update a one
	PostDto updatePost(PostDto postDto, Integer postId);
	
	//Delete one post by Id
	void deletePost(Integer postId);
	
	//Get All post will return List
	PostResponse getAllPost(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
	
	//Get one post By Id
	PostDto getPostById(Integer postId);
	
	//Get posts by CagetoryId
	List<PostDto> getPostByCategory(Integer categoryId);
	
	//Get Posts by UserId
	List<PostDto> getPostByUser(Integer userId);
	
	//Get All post by Keywords
	List<PostDto> searchPost(String keyword);

	void toggleLikePost(Integer postId, Integer userId);

	long getPostLikeCount(Integer postId);

}
