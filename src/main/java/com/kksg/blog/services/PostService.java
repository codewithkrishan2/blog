package com.kksg.blog.services;

import com.kksg.blog.entities.Post;
import com.kksg.blog.entities.enums.PostStatus;
import com.kksg.blog.payloads.PostAnalyticsDto;
import com.kksg.blog.payloads.PostDto;
import com.kksg.blog.payloads.PostResponse;
import com.kksg.blog.payloads.UserAnalyticsDto;

public interface PostService {

	PostDto createOrUpdatePost(PostDto postDto, Integer userId, Integer categoryId);

	PostDto updatePost(PostDto postDto, Integer postId);

	void deletePost(Integer postId);

	PostResponse getAllPost(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

	PostDto getPostById(Integer postId);

	void incrementViewCount(Post post);

	void toggleLikePost(Integer postId, Integer userId);

	long getPostLikeCount(Integer postId);

	PostAnalyticsDto getPostAnalytics(Integer postId);

	UserAnalyticsDto getUserAnalytics(Integer userId);

	PostDto updatePostStatus(Integer postId, PostStatus newStatus);

	PostResponse searchPostsByTag(String tagName, Integer pageNumber, Integer pageSize);

	PostResponse getTrendingPosts(Integer pageNumber, Integer pageSize);

	PostResponse searchPost(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

	PostResponse getPostByUser(Integer userId, Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

	PostResponse getPostByCategory(Integer categoryId, Integer pageNumber, Integer pageSize, String sortBy,
			String sortDir);

	PostDto getPostBySlug(String slug);


}
