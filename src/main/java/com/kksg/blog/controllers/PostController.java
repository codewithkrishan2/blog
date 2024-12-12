package com.kksg.blog.controllers;

import java.io.IOException;
import java.io.InputStream;

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
import com.kksg.blog.payloads.ApiResponse;
import com.kksg.blog.payloads.PostAnalyticsDto;
import com.kksg.blog.payloads.PostDto;
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
	public ResponseEntity<ApiResponse> createPost(@RequestBody PostDto postDto, @PathVariable Integer userId,
			@PathVariable Integer categoryId) {
		try {
			PostDto createdPost = this.postService.createPost(postDto, userId, categoryId);
			ApiResponse apiResponse = new ApiResponse(AppConstants.SUCCESS, null, "Post Created Successfully",
					createdPost);
			return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.CREATED);
		} catch (Exception e) {
			ApiResponse apiResponse = new ApiResponse(AppConstants.FAILED, e.getMessage(), null, null);
			return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.BAD_REQUEST);
		}
	}

	// Api for changing the post status
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/post/{postId}/status")
	public ResponseEntity<ApiResponse> updatePostStatus(@PathVariable Integer postId,
			@RequestParam PostStatus newStatus) {
		ApiResponse apiResponse = null;
		try {
			PostDto updatedPost = this.postService.updatePostStatus(postId, newStatus);
			apiResponse = new ApiResponse(AppConstants.SUCCESS, null, "Post Status Updated Successfully", updatedPost);
		} catch (Exception e) {
			apiResponse = new ApiResponse(AppConstants.FAILED, e.getMessage(), null, null);
			return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
	}

//	// Get Posts By User
//	@GetMapping("/user/{userId}/posts")
//	public ResponseEntity<ApiResponse> getPostsByUser(@PathVariable Integer userId) {
//		ApiResponse apiResponse = null;
//		try {
//		List<PostDto> postsByUser = this.postService.getPostByUser(userId);
//		apiResponse = new ApiResponse(AppConstants.SUCCESS, null, "Successfully fetched posts", postsByUser);		
//		} catch (Exception e) {
//			apiResponse = new ApiResponse(AppConstants.FAILED, e.getMessage(), null, null);
//			return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.BAD_REQUEST);
//		}
//		return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
//	}
	
	@GetMapping("/user/{userId}/posts")
	public ResponseEntity<ApiResponse> getPostsByUser(@PathVariable Integer userId,
	        @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
	        @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
	        @RequestParam(defaultValue = "postId", required = false) String sortBy,
	        @RequestParam(defaultValue = AppConstants.SORT_DIR, required = false) String sortDir) {

	    ApiResponse apiResponse = null;
	    try {
	        PostResponse postResponse = this.postService.getPostByUser(userId, pageNumber, pageSize, sortBy, sortDir);
	        apiResponse = new ApiResponse(AppConstants.SUCCESS, null, "Successfully fetched posts", postResponse);
	    } catch (Exception e) {
	        apiResponse = new ApiResponse(AppConstants.FAILED, e.getMessage(), null, null);
	        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
	    }
	    return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}


//	// Get posts By Category
//	@GetMapping("/categories/{categoryId}/posts")
//	public ResponseEntity<ApiResponse> getPostsByCategory(@PathVariable Integer categoryId) {
//		ApiResponse apiResponse = null;
//		try {
//			List<PostDto> postsByCategory = this.postService.getPostByCategory(categoryId);
//			apiResponse = new ApiResponse(AppConstants.SUCCESS, null, "Successfully fetched posts", postsByCategory);
//		} catch (Exception e) {
//			apiResponse = new ApiResponse(AppConstants.FAILED, e.getMessage(), null, null);
//			return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.BAD_REQUEST);
//		}
//		return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
//	}

	@GetMapping("/categories/{categoryId}/posts")
	public ResponseEntity<ApiResponse> getPostsByCategory(@PathVariable Integer categoryId,
	        @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
	        @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
	        @RequestParam(defaultValue = "postId", required = false) String sortBy,
	        @RequestParam(defaultValue = AppConstants.SORT_DIR, required = false) String sortDir) {

	    ApiResponse apiResponse = null;
	    try {
	        PostResponse postResponse = this.postService.getPostByCategory(categoryId, pageNumber, pageSize, sortBy, sortDir);
	        apiResponse = new ApiResponse(AppConstants.SUCCESS, null, "Successfully fetched posts", postResponse);
	    } catch (Exception e) {
	        apiResponse = new ApiResponse(AppConstants.FAILED, e.getMessage(), null, null);
	        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
	    }
	    return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	
	// Get All Posts
	@GetMapping("/posts/all")
	public ResponseEntity<ApiResponse> getAllPost(
			@RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
			@RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
			@RequestParam(defaultValue = "postId", required = false) String sortBy,
			@RequestParam(defaultValue = AppConstants.SORT_DIR, required = false) String sortDir
	) {
		ApiResponse apiResponse = null;
		try {
			PostResponse postResponse = this.postService.getAllPost(pageNumber, pageSize, sortBy, sortDir);
			apiResponse = new ApiResponse(AppConstants.SUCCESS, null, "Successful", postResponse);
		} catch (Exception e) {
			apiResponse = new ApiResponse(AppConstants.FAILED, e.getCause().getMessage(), null, null);
			return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.BAD_REQUEST);			
		}
		return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
	}

	// Get Post By Post Id
	@GetMapping("/post/{postId}")
	public ResponseEntity<ApiResponse> getPostById(@PathVariable Integer postId) {
		ApiResponse apiResponse = null;
		try {
			PostDto postById = this.postService.getPostById(postId);
			apiResponse = new ApiResponse(AppConstants.SUCCESS, null, "Successfully fetched post", postById);
		} catch (Exception e) {
			apiResponse = new ApiResponse(AppConstants.FAILED, e.getMessage(), null, null);
			return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
	}

	// Delete one Post
	@DeleteMapping("/post/delete/{postId}")
	public ResponseEntity<ApiResponse> deletePostById(@PathVariable Integer postId) {
		ApiResponse apiResponse = null;
		try {
			this.postService.deletePost(postId);
			apiResponse =  new ApiResponse(AppConstants.SUCCESS, null, "Successfully Delete", null);
		} catch (Exception e) {
			apiResponse = new ApiResponse(AppConstants.FAILED, e.getMessage(), null, null);
			return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
	}

	@PutMapping("/post/update/{postId}")
	public ResponseEntity<ApiResponse> updatePostById(@Valid @RequestBody PostDto postDto, @PathVariable Integer postId) {
		ApiResponse apiResponse = null;
		try {
			PostDto updatedPost = this.postService.updatePost(postDto, postId);
			apiResponse = new ApiResponse(AppConstants.SUCCESS, null, "Post Updated Successfully", updatedPost);
		} catch (Exception e) {
			apiResponse = new ApiResponse(AppConstants.FAILED, e.getMessage(), null, null);
			return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
	}

//	 // Search post by Title containing
//    @GetMapping("/posts/search/{keywords}")
//    public ResponseEntity<ApiResponse> searchPostByTitle(@PathVariable String keywords) {
//        try {
//            List<PostDto> postDtos = this.postService.searchPost(keywords);
//            ApiResponse apiResponse = new ApiResponse(AppConstants.SUCCESS, null, "Successfully fetched posts", postDtos);
//            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
//        } catch (Exception e) {
//            ApiResponse apiResponse = new ApiResponse(AppConstants.FAILED, e.getMessage(), null, null);
//            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
//        }
//    }
	
	// Search post by Title containing
	@GetMapping("/posts/search/{keywords}")
	public ResponseEntity<ApiResponse> searchPostByTitle(
	        @PathVariable String keywords,
	        @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
	        @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
	        @RequestParam(defaultValue = "postTitle", required = false) String sortBy,
	        @RequestParam(defaultValue = AppConstants.SORT_DIR, required = false) String sortDir) {

	    ApiResponse apiResponse = null;
	    try {
	        PostResponse postResponse = this.postService.searchPost(keywords, pageNumber, pageSize, sortBy, sortDir);
	        apiResponse = new ApiResponse(AppConstants.SUCCESS, null, "Successfully fetched posts", postResponse);
	    } catch (Exception e) {
	        apiResponse = new ApiResponse(AppConstants.FAILED, e.getMessage(), null, null);
	        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
	    }
	    return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}


    // Post image Upload
    @PostMapping("/post/image/upload/{postId}")
    public ResponseEntity<ApiResponse> uploadPostImages(@RequestParam MultipartFile image, @PathVariable Integer postId)
            throws IOException {
        try {
            PostDto postById = this.postService.getPostById(postId);
            String fileName = this.fileService.uploadImage(path, image);
            postById.setPostImage(fileName);
            PostDto updatedPost = this.postService.updatePost(postById, postId);
            ApiResponse apiResponse = new ApiResponse(AppConstants.SUCCESS, null, "Image Uploaded Successfully", updatedPost);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse(AppConstants.FAILED, e.getMessage(), null, null);
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
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
    public ResponseEntity<ApiResponse> likePost(@PathVariable Integer postId, @RequestParam Integer userId) {
        try {
            postService.toggleLikePost(postId, userId);
            ApiResponse apiResponse = new ApiResponse(AppConstants.SUCCESS, null, "Post Liked Successfully", null);
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse(AppConstants.FAILED, e.getMessage(), null, null);
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }

    // Get like count
    @GetMapping("/post/{postId}/like-count")
    public ResponseEntity<ApiResponse> getPostLikeCount(@PathVariable Integer postId) {
        try {
            long likeCount = postService.getPostLikeCount(postId);
            ApiResponse apiResponse = new ApiResponse(AppConstants.SUCCESS, null, "Successfully fetched like count", likeCount);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse(AppConstants.FAILED, e.getMessage(), null, null);
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/post/{postId}/analytics")
    public ResponseEntity<ApiResponse> getPostAnalytics(@PathVariable Integer postId) {
        try {
            PostAnalyticsDto postAnalytics = postService.getPostAnalytics(postId);
            ApiResponse apiResponse = new ApiResponse(AppConstants.SUCCESS, null, "Successfully fetched post analytics", postAnalytics);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse(AppConstants.FAILED, e.getMessage(), null, null);
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user/{userId}/analytics")
    public ResponseEntity<ApiResponse> getUserAnalytics(@PathVariable Integer userId) {
        try {
            UserAnalyticsDto userAnalytics = postService.getUserAnalytics(userId);
            ApiResponse apiResponse = new ApiResponse(AppConstants.SUCCESS, null, "Successfully fetched user analytics", userAnalytics);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse(AppConstants.FAILED, e.getMessage(), null, null);
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }

//    @GetMapping("/post/trending")
//    public ResponseEntity<ApiResponse> getTrendingPosts() {
//        try {
//            List<PostListDto> trendingPosts = postService.getTrendingPosts();
//            ApiResponse apiResponse = new ApiResponse(AppConstants.SUCCESS, null, "Successfully fetched trending posts", trendingPosts);
//            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
//        } catch (Exception e) {
//            ApiResponse apiResponse = new ApiResponse(AppConstants.FAILED, e.getMessage(), null, null);
//            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
//        }
//    }
    
    @GetMapping("/post/trending")
    public ResponseEntity<ApiResponse> getTrendingPosts(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = "postAddedDate", required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIR, required = false) String sortDir) {

        ApiResponse apiResponse = null;
        try {
            PostResponse postResponse = this.postService.getTrendingPosts(pageNumber, pageSize);
            apiResponse = new ApiResponse(AppConstants.SUCCESS, null, "Successfully fetched trending posts", postResponse);
        } catch (Exception e) {
            apiResponse = new ApiResponse(AppConstants.FAILED, e.getMessage(), null, null);
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


//    @GetMapping("/post/searchByTag")
//    public ResponseEntity<ApiResponse> searchPostsByTag(@RequestParam String tagName) {
//        try {
//            List<PostListDto> posts = postService.searchPostsByTag(tagName);
//            ApiResponse apiResponse = new ApiResponse(AppConstants.SUCCESS, null, "Successfully fetched posts by tag", posts);
//            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
//        } catch (Exception e) {
//            ApiResponse apiResponse = new ApiResponse(AppConstants.FAILED, e.getMessage(), null, null);
//            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
//        }
//    }
    
    @GetMapping("/post/searchByTag")
    public ResponseEntity<ApiResponse> searchPostsByTag(
            @RequestParam String tagName,
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = "postAddedDate", required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIR, required = false) String sortDir) {
        ApiResponse apiResponse = null;
        try {
            PostResponse postResponse = this.postService.searchPostsByTag(tagName, pageNumber, pageSize);
            apiResponse = new ApiResponse(AppConstants.SUCCESS, null, "Successfully fetched posts by tag", postResponse);
        } catch (Exception e) {
            apiResponse = new ApiResponse(AppConstants.FAILED, e.getMessage(), null, null);
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
