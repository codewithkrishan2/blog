package com.kksg.blog.services.impl;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kksg.blog.entities.Category;
import com.kksg.blog.entities.Like;
import com.kksg.blog.entities.Post;
import com.kksg.blog.entities.User;
import com.kksg.blog.entities.enums.PostStatus;
import com.kksg.blog.exceptions.ResourceNotFoundException;
import com.kksg.blog.payloads.PostDto;
import com.kksg.blog.payloads.PostListDto;
import com.kksg.blog.payloads.PostResponse;
import com.kksg.blog.repositories.CategoryRepo;
import com.kksg.blog.repositories.LikeRepository;
import com.kksg.blog.repositories.PostRepo;
import com.kksg.blog.repositories.UserRepo;
import com.kksg.blog.services.PostService;
import com.kksg.blog.utils.PaginationUtil;
import com.kksg.blog.utils.SlugUtil;

@Service
public class PostServiceImpl implements PostService {

	@Autowired
	private PostRepo postRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private CategoryRepo categoryRepo;

	@Autowired
	private LikeRepository likeRepo;

	@Override
	public PostDto createPost(PostDto postDto, Integer userId, Integer categoryId) {

		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "User Id", userId));

		Category category = this.categoryRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", categoryId));
		Post post = this.modelMapper.map(postDto, Post.class);

		// Set default values for SEO fields if not provided
		if (post.getMetaTitle() == null || post.getMetaTitle().isEmpty()) {
			post.setMetaTitle(post.getPostTitle()); // Use the title as the meta title by default
		}
		if (post.getMetaDescription() == null || post.getMetaDescription().isEmpty()) {
			post.setMetaDescription(post.getPostContent().substring(0, Math.min(150, post.getPostContent().length()))); // Use
																														// a
																														// snippet
																														// from
																														// the
																														// content
		}
		if (post.getMetaKeywords() == null || post.getMetaKeywords().isEmpty()) {
			post.setMetaKeywords(SlugUtil.generateKeywords(post.getPostContent())); // Optionally generate keywords from
																					// content
		}

		// Generate a slug from the post title
		String generatedSlug = SlugUtil.generateSlug(post.getPostTitle());
		post.setSlug(ensureUniqueSlug(generatedSlug));

		post.setPostImage("default.jpg");
		post.setPostAddedDate(new Date());
		post.setUser(user);
		post.setPostCategory(category);
		post.setStatus(PostStatus.PENDING);

		Post newPost = this.postRepo.save(post);

		return this.modelMapper.map(newPost, PostDto.class);
	}

	private String ensureUniqueSlug(String slug) {
		Optional<Post> existingPost = postRepo.findBySlug(slug);
		if (existingPost.isPresent()) {
			int count = 1;
			String newSlug = slug + "-" + count;
			while (postRepo.existsBySlug(newSlug)) {
				count++;
				newSlug = slug + "-" + count;
			}
			return newSlug;
		}
		return slug;
	}

	@Override
	public void toggleLikePost(Integer postId, Integer userId) {
		// Fetch the post and user from the repository
		Post post = postRepo.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post", "Post Id", postId));
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "User Id", userId));

		// Check if the user has already liked the post
		Optional<Like> existingLike = likeRepo.findByPostAndUser(post, user);

		if (existingLike.isPresent()) {
			// If the like exists, remove the like (unlike)
			likeRepo.delete(existingLike.get());
		} else {
			// If the like does not exist, create a new like (like the post)
			Like newLike = new Like();
			newLike.setPost(post);
			newLike.setUser(user);
			newLike.setLikeDate(LocalDateTime.now());

			// Save the new like to the repository
			likeRepo.save(newLike);
		}
	}

	@Override
	public long getPostLikeCount(Integer postId) {
		Post post = postRepo.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post", "Post Id", postId));
		return likeRepo.countByPost(post); // Return the like count for the post
	}

	@Override
	public PostDto updatePost(PostDto postDto, Integer postId) {
		Post post = this.postRepo.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post ", "Post Id", postId));
		Category category = categoryRepo.findById(postDto.getPostCategory().getCategoryId()).get();

		post.setPostTitle(postDto.getPostTitle());
		post.setPostContent(postDto.getPostContent());
		post.setPostImage(postDto.getPostImage());
		post.setPostCategory(category);
		Post updatedPost = this.postRepo.save(post);
		return this.modelMapper.map(updatedPost, PostDto.class);
	}

	@Override
	public void deletePost(Integer postId) {
		Post post = this.postRepo.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post ", "Post Id", postId));
		System.out.println("________________________________________________________________");
		post.getComments().clear();
		this.postRepo.delete(post);
	}

	@Override
	public PostResponse getAllPost(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {

		// this is for sorting
//		Sort sort = (sortDir.equalsIgnoreCase("asc"))?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
//		Pageable p = PageRequest.of(pageNumber, pageSize, sort);
		Pageable pageable = PaginationUtil.createPageRequest(pageNumber, pageSize, sortBy, sortDir);
		Page<Post> page = this.postRepo.findAll(pageable);
		List<Post> posts = page.getContent();
		List<PostListDto> postDtos = posts.stream().map((post) -> {
			PostListDto postListDto = this.modelMapper.map(post, PostListDto.class);
			postListDto.setUserName(post.getUser().getName());
			postListDto.setCommentsCount(post.getComments().size());
			Integer likeCount = likeRepo.countByPost(post);
			postListDto.setLikeCount(likeCount);
			return postListDto;
		}).collect(Collectors.toList());
		
		PostResponse postResponse = new PostResponse();

		postResponse.setContent(postDtos);
		postResponse.setPageNumber(page.getNumber());
		postResponse.setPageSize(page.getSize());
		postResponse.setTotalElements(page.getTotalElements());
		postResponse.setTotalPages(page.getTotalPages());
		postResponse.setFirstPage(page.isFirst());
		postResponse.setLastPage(page.isLast());
		return postResponse;
	}

	@Override
	public PostDto getPostById(Integer postId) {
		Post post = this.postRepo.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post", "Post Id", postId));
		PostDto postDto = modelMapper.map(post, PostDto.class);

		// Manually count the likes for this post
	    Integer likeCount = likeRepo.countByPost(post);
	    // Set the likeCount in the PostDto
	    postDto.setLikeCount(likeCount);
	    
		return postDto;
	}

	@Override
	public List<PostDto> getPostByCategory(Integer categoryId) {
		Category category = this.categoryRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", categoryId));
		List<Post> postsByCategory = this.postRepo.findByPostCategory(category);
		List<PostDto> postDtos = postsByCategory.stream().map((post) -> this.modelMapper.map(post, PostDto.class))
				.collect(Collectors.toList());
		return postDtos;
	}

	@Override
	public List<PostDto> getPostByUser(Integer userId) {
		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "User Id", userId));
		List<Post> postsByUser = this.postRepo.findByUser(user);
		List<PostDto> postDtos = postsByUser.stream().map((post) -> this.modelMapper.map(post, PostDto.class))
				.collect(Collectors.toList());
		return postDtos;
	}

	@Override
	public List<PostDto> searchPost(String keyword) {
		List<Post> posts = this.postRepo.findByPostTitleContaining(keyword);
		List<PostDto> postDtos = posts.stream().map((post) -> modelMapper.map(post, PostDto.class))
				.collect(Collectors.toList());

		return postDtos;
	}

}
