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

import com.kksg.blog.config.AppConstants;
import com.kksg.blog.payloads.PostDto;
import com.kksg.blog.payloads.PostResponse;
import com.kksg.blog.services.FileService;
import com.kksg.blog.services.PostService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class PostController {

	@Autowired
	private PostService postService;
	
	@Autowired
	private FileService fileService;
	
	//Getting path of the imagesFile
	
	@Value("${project.image}")
	private String path;
	
	
	//Create Post By User Id And Category Id
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/user/{userId}/category/{categoryId}/createPost")
	public ResponseEntity<PostDto> createPost(
			@RequestBody PostDto postDto, 
			@PathVariable Integer userId, 
			@PathVariable Integer categoryId){
		PostDto createdPost = this.postService.createPost(postDto, userId, categoryId);
		return new ResponseEntity<PostDto>(createdPost, HttpStatus.CREATED);
	}
	
	//Get Posts By User
	@GetMapping("/user/{userId}/posts")
	public ResponseEntity<List<PostDto>> getPostsByUser(@PathVariable Integer userId){
		
		List<PostDto> postsByUser = this.postService.getPostByUser(userId);
		
		return new ResponseEntity<List<PostDto>>(postsByUser, HttpStatus.OK);
	}
	
	//Get posts By Category
	@GetMapping("/categories/{categoryId}/posts")
	public ResponseEntity<List<PostDto>> getPostsByCategory(@PathVariable Integer categoryId){
		
		List<PostDto> postsByCategory = this.postService.getPostByCategory(categoryId);
		return new ResponseEntity<List<PostDto>>(postsByCategory, HttpStatus.OK);
	}
	
	//Get All Posts
	@GetMapping("/posts/all")
	public ResponseEntity<PostResponse> getAllPost(
			@RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
			@RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
			@RequestParam(value = "sortBy", defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
			@RequestParam(value = "sortDir", defaultValue = AppConstants.SORT_DIR, required = false) String sortDir){
		
		PostResponse postResponse = this.postService.getAllPost(pageNumber,pageSize,sortBy,sortDir);
		return new ResponseEntity<PostResponse>(postResponse, HttpStatus.OK);
	}
	
	//Get Post By Post Id
	@GetMapping("/post/{postId}")
	public ResponseEntity<PostDto> getPostById(@PathVariable Integer postId){
		
		 PostDto postById = this.postService.getPostById(postId);
		return new ResponseEntity<PostDto>(postById, HttpStatus.OK);
	}
	
	//Delete one Post
	@DeleteMapping("/post/delete/{postId}")
	public ResponseEntity<String> deletePostById(@PathVariable Integer postId){
		this.postService.deletePost(postId);
		return new ResponseEntity<String>("Successfully Delete", HttpStatus.OK);
	}
	
	@PutMapping("/post/update/{postId}")
	public ResponseEntity<PostDto> updatePostById(
			@Valid @RequestBody PostDto postDto, 
			@PathVariable Integer postId) {
		
		PostDto updatedPost = this.postService.updatePost(postDto, postId);
		return new ResponseEntity<PostDto>(updatedPost, HttpStatus.OK);
	}
	
	//Search post by Title containing
	@GetMapping("/posts/search/{keywords}")
	public ResponseEntity<List<PostDto>> searchPostByTitle(@PathVariable String keywords) {
		List<PostDto> postDtos = this.postService.searchPost(keywords);
		return new ResponseEntity<List<PostDto>>(postDtos, HttpStatus.OK);
	}
	
	
	//Post image Upload
	@PostMapping("/post/image/upload/{postId}")
	public ResponseEntity<PostDto> uploadPostImages(
			@RequestParam("image") MultipartFile  image,
			@PathVariable Integer postId ) throws IOException{
		
		PostDto postById = this.postService.getPostById(postId);
		
		String fileName = this.fileService.uploadImage(path, image);
		postById.setPostImage(fileName);
		PostDto updatedPost = this.postService.updatePost(postById, postId);
		return new ResponseEntity<PostDto>(updatedPost, HttpStatus.OK);
	}
	
	//Method to serve uploaded images:
	@GetMapping(value = "/post/image/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
	public void downloadImage(
			@PathVariable String imageName,
			HttpServletResponse response) throws IOException {
		InputStream resource = this.fileService.getResources(path, imageName);
		response.setContentType(MediaType.IMAGE_JPEG_VALUE);
		StreamUtils.copy(resource, response.getOutputStream());
	}
}
