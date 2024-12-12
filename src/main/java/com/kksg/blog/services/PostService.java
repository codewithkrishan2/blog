package com.kksg.blog.services;

import com.kksg.blog.entities.Post;
import com.kksg.blog.entities.enums.PostStatus;
import com.kksg.blog.payloads.PostAnalyticsDto;
import com.kksg.blog.payloads.PostDto;
import com.kksg.blog.payloads.PostResponse;
import com.kksg.blog.payloads.UserAnalyticsDto;

public interface PostService {

	// Create a post
	PostDto createPost(PostDto postDto, Integer userId, Integer categoryId);

	// Update a one
	PostDto updatePost(PostDto postDto, Integer postId);

	// Delete one post by Id
	void deletePost(Integer postId);

	// Get All post will return List
	PostResponse getAllPost(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

	// Get one post By Id
	PostDto getPostById(Integer postId);

	// Get posts by CagetoryId
//	List<PostDto> getPostByCategory(Integer categoryId);

	// Get Posts by UserId
//	List<PostDto> getPostByUser(Integer userId);

	// Get All post by Keywords
//	List<PostDto> searchPost(String keyword);

	void incrementViewCount(Post post);

	void toggleLikePost(Integer postId, Integer userId);

	long getPostLikeCount(Integer postId);

	PostAnalyticsDto getPostAnalytics(Integer postId);

	UserAnalyticsDto getUserAnalytics(Integer userId);

//	List<PostListDto> getTrendingPosts();

	PostDto updatePostStatus(Integer postId, PostStatus newStatus);

//	List<PostListDto> searchPostsByTag(String tagName);

	PostResponse searchPostsByTag(String tagName, Integer pageNumber, Integer pageSize);

	PostResponse getTrendingPosts(Integer pageNumber, Integer pageSize);

	PostResponse searchPost(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

	PostResponse getPostByUser(Integer userId, Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

	PostResponse getPostByCategory(Integer categoryId, Integer pageNumber, Integer pageSize, String sortBy,
			String sortDir);

}
