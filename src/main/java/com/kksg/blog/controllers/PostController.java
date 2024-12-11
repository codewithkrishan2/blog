package com.kksg.blog.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kksg.blog.entities.enums.PostStatus;
import com.kksg.blog.payloads.PostAnalyticsDto;
import com.kksg.blog.payloads.PostDto;
import com.kksg.blog.payloads.PostListDto;
import com.kksg.blog.payloads.PostResponse;
import com.kksg.blog.payloads.UserAnalyticsDto;
import com.kksg.blog.services.FileService;
import com.kksg.blog.services.PostService;
import com.kksg.blog.utils.AppConstants;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class PostController {

	@Autowired
	private PostService postService;

	@Autowired
	private FileService fileService;

	// Getting path of the imagesFile
	@Value("${project.image}")
	private String path;

	// Create Post By User Id And Category Id
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/user/{userId}/category/{categoryId}/createPost")
	public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto, @PathVariable Integer userId,
			@PathVariable Integer categoryId) {
		PostDto createdPost = this.postService.createPost(postDto, userId, categoryId);
		return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
	}
	
	//Api for changing the post status
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/post/{postId}/status")
	public ResponseEntity<PostDto> updatePostStatus(@PathVariable Integer postId, @RequestParam PostStatus newStatus) {
		PostDto updatedPost = this.postService.updatePostStatus(postId, newStatus);
		return new ResponseEntity<>(updatedPost, HttpStatus.OK);
	}

	// Get Posts By User
	@GetMapping("/user/{userId}/posts")
	public ResponseEntity<List<PostDto>> getPostsByUser(@PathVariable Integer userId) {

		List<PostDto> postsByUser = this.postService.getPostByUser(userId);

		return new ResponseEntity<List<PostDto>>(postsByUser, HttpStatus.OK);
	}

	// Get posts By Category
	@GetMapping("/categories/{categoryId}/posts")
	public ResponseEntity<List<PostDto>> getPostsByCategory(@PathVariable Integer categoryId) {

		List<PostDto> postsByCategory = this.postService.getPostByCategory(categoryId);
		return new ResponseEntity<List<PostDto>>(postsByCategory, HttpStatus.OK);
	}

	// Get All Posts
	@GetMapping("/posts/all")
	public ResponseEntity<PostResponse> getAllPost(
			@RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
			@RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
			@RequestParam(defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
			@RequestParam(defaultValue = AppConstants.SORT_DIR, required = false) String sortDir

	) {
		PostResponse postResponse = this.postService.getAllPost(pageNumber, pageSize, sortBy, sortDir);
		return new ResponseEntity<PostResponse>(postResponse, HttpStatus.OK);
	}

	// Get Post By Post Id
	@GetMapping("/post/{postId}")
	public ResponseEntity<PostDto> getPostById(@PathVariable Integer postId) {

		PostDto postById = this.postService.getPostById(postId);
		return new ResponseEntity<PostDto>(postById, HttpStatus.OK);
	}

	// Delete one Post
	@DeleteMapping("/post/delete/{postId}")
	public ResponseEntity<String> deletePostById(@PathVariable Integer postId) {
		this.postService.deletePost(postId);
		return new ResponseEntity<String>("Successfully Delete", HttpStatus.OK);
	}

	@PutMapping("/post/update/{postId}")
	public ResponseEntity<PostDto> updatePostById(@Valid @RequestBody PostDto postDto, @PathVariable Integer postId) {

		PostDto updatedPost = this.postService.updatePost(postDto, postId);
		return new ResponseEntity<PostDto>(updatedPost, HttpStatus.OK);
	}

	// Search post by Title containing
	@GetMapping("/posts/search/{keywords}")
	public ResponseEntity<List<PostDto>> searchPostByTitle(@PathVariable String keywords) {
		List<PostDto> postDtos = this.postService.searchPost(keywords);
		return new ResponseEntity<List<PostDto>>(postDtos, HttpStatus.OK);
	}

	// Post image Upload
	@PostMapping("/post/image/upload/{postId}")
	public ResponseEntity<PostDto> uploadPostImages(@RequestParam MultipartFile image, @PathVariable Integer postId)
			throws IOException {

		PostDto postById = this.postService.getPostById(postId);

		String fileName = this.fileService.uploadImage(path, image);
		postById.setPostImage(fileName);
		PostDto updatedPost = this.postService.updatePost(postById, postId);
		return new ResponseEntity<PostDto>(updatedPost, HttpStatus.OK);
	}

	// Method to serve uploaded images:
	@GetMapping(value = "/post/image/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
	public void downloadImage(@PathVariable String imageName, HttpServletResponse response) throws IOException {
		InputStream resource = this.fileService.getResources(path, imageName);
		response.setContentType(MediaType.IMAGE_JPEG_VALUE);
		StreamUtils.copy(resource, response.getOutputStream());
	}

	// Like a post
	@PostMapping("/post/{postId}/like")
	public ResponseEntity<Void> likePost(@PathVariable Integer postId, @RequestParam Integer userId) {
		postService.toggleLikePost(postId, userId);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	// get like count
	@GetMapping("/post/{postId}/like-count")
	public ResponseEntity<Long> getPostLikeCount(@PathVariable Integer postId) {
		long likeCount = postService.getPostLikeCount(postId);
		return ResponseEntity.ok(likeCount); // Return 200 OK with the like count
	}

	@GetMapping("/post/{postId}/analytics")
	public PostAnalyticsDto getPostAnalytics(@PathVariable Integer postId) {
		return postService.getPostAnalytics(postId);
	}

	@GetMapping("/user/{userId}/analytics")
	public UserAnalyticsDto getUserAnalytics(@PathVariable Integer userId) {
		return postService.getUserAnalytics(userId);
	}

	@GetMapping("/post/trending")
	public List<PostListDto> getTrendingPosts() {
		return postService.getTrendingPosts();
	}	

	@GetMapping("/post/searchByTag")
    public ResponseEntity<List<PostListDto>> searchPostsByTag(@RequestParam String tagName) {
        List<PostListDto> posts = postService.searchPostsByTag(tagName);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }
}
