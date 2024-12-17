package com.kksg.blog.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kksg.blog.entities.Category;
import com.kksg.blog.entities.Likes;
import com.kksg.blog.entities.Post;
import com.kksg.blog.entities.Tag;
import com.kksg.blog.entities.User;
import com.kksg.blog.entities.enums.PostStatus;
import com.kksg.blog.exceptions.ResourceNotFoundException;
import com.kksg.blog.payloads.PostAnalyticsDto;
import com.kksg.blog.payloads.PostDto;
import com.kksg.blog.payloads.PostListDto;
import com.kksg.blog.payloads.PostResponse;
import com.kksg.blog.payloads.TagDto;
import com.kksg.blog.payloads.UserAnalyticsDto;
import com.kksg.blog.repositories.CategoryRepo;
import com.kksg.blog.repositories.CommentsRepo;
import com.kksg.blog.repositories.LikeRepository;
import com.kksg.blog.repositories.PostRepo;
import com.kksg.blog.repositories.TagRepository;
import com.kksg.blog.repositories.UserRepo;
import com.kksg.blog.services.NotificationService;
import com.kksg.blog.services.PostService;
import com.kksg.blog.utils.ContentSanitizer;
import com.kksg.blog.utils.PaginationUtil;
import com.kksg.blog.utils.SlugUtil;

import jakarta.transaction.Transactional;

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

	@Autowired
	private CommentsRepo commentsRepo;

	@Autowired
	private ContentSanitizer contentSanitizer;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private TagRepository tagRepository;

	@Override
	@Transactional
	public PostDto createOrUpdatePost(PostDto postDto, Integer userId, Integer categoryId) {

		String sanitizedContent = contentSanitizer.sanitizeContent(postDto.getPostContent());
		postDto.setPostContent(sanitizedContent);

		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "User Id", userId));
		Category category = this.categoryRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", categoryId));

		Post post;
		if (postDto.getPostId() != null) {
			post = this.postRepo.findById(postDto.getPostId())
					.orElseThrow(() -> new ResourceNotFoundException("Post", "Post Id", postDto.getPostId()));
			if (post.getStatus() != PostStatus.DRAFT) {
				if (post.getStatus() == PostStatus.PUBLISHED) {
					post.setStatus(PostStatus.PENDING);
				}
			}
		} else {
			post = new Post();
		}

		// Set or update the post fields
		post.setPostTitle(postDto.getPostTitle());
		post.setPostContent(postDto.getPostContent());
		post.setPostCategory(category);
		post.setUser(user);
		post.setPostAddedDate(LocalDateTime.now());

		// Handle SEO fields if not provided
		if (post.getMetaTitle() == null || post.getMetaTitle().isEmpty()) {
			post.setMetaTitle(post.getPostTitle());
		}
		if (post.getMetaDescription() == null || post.getMetaDescription().isEmpty()) {
			post.setMetaDescription(post.getPostContent().substring(0, Math.min(150, post.getPostContent().length())));
		}
		if (post.getMetaKeywords() == null || post.getMetaKeywords().isEmpty()) {
			post.setMetaKeywords(SlugUtil.generateKeywords(post.getPostContent()));
		}
		//String generatedSlug = SlugUtil.generateSlug(post.getPostTitle());
		//post.setSlug(ensureUniqueSlug(generatedSlug));

		// Handle SEO fields - Use values from payload if provided, else generate defaults
	    if (postDto.getMetaTitle() != null && !postDto.getMetaTitle().isEmpty()) {
	        post.setMetaTitle(postDto.getMetaTitle());
	    } else {
	        // Set default meta title (same as post title)
	        post.setMetaTitle(post.getPostTitle());
	    }

	    if (postDto.getMetaDescription() != null && !postDto.getMetaDescription().isEmpty()) {
	        post.setMetaDescription(postDto.getMetaDescription());
	    } else {
	        // Generate default meta description (first 150 characters of content)
	        post.setMetaDescription(post.getPostContent().substring(0, Math.min(150, post.getPostContent().length())));
	    }

	    if (postDto.getMetaKeywords() != null && !postDto.getMetaKeywords().isEmpty()) {
	        post.setMetaKeywords(postDto.getMetaKeywords());
	    } else {
	        // Generate default keywords (based on content)
	        post.setMetaKeywords(SlugUtil.generateKeywords(post.getPostContent()));
	    }

	    // Set slug - Use provided slug if exists, else generate it
	    if (postDto.getSlug() != null && !postDto.getSlug().isEmpty()) {
	        post.setSlug(ensureUniqueSlug(postDto.getSlug()));
	    } else {
	        String generatedSlug = SlugUtil.generateSlug(post.getPostTitle());
	        post.setSlug(ensureUniqueSlug(generatedSlug)); // Ensure the slug is unique
	    }
		
		
		if (postDto.getTags() != null && !postDto.getTags().isEmpty()) {
			Set<Tag> tags = postDto.getTags().stream().map(tagDto -> {
				return tagRepository.findByTagName(tagDto.getTagName()).orElseGet(() -> {
					Tag newTag = new Tag();
					newTag.setTagName(tagDto.getTagName());
					return tagRepository.save(newTag);
				});
			}).collect(Collectors.toSet());
			post.setTags(tags);
		}


		// Set default post image if not provided
		if (post.getPostImage() == null || post.getPostImage().isEmpty()) {
			post.setPostImage("default.jpg");
		}

		if (postDto.getStatus() == PostStatus.DRAFT) {
			post.setStatus(PostStatus.DRAFT);
		} else {
			post.setStatus(PostStatus.PENDING);
		}

		Post savedPost = this.postRepo.save(post);

//		if (savedPost.getStatus() != PostStatus.DRAFT) {
//			notificationService.sendPostStatusNotification(user, savedPost);
//		}

		PostDto responsePostDto = this.modelMapper.map(savedPost, PostDto.class);

		if (savedPost.getTags() != null) {
			responsePostDto.setTags(savedPost.getTags().stream()
					.map(tag -> new TagDto(tag.getTagId(), tag.getTagName())).collect(Collectors.toSet()));
		}

		return responsePostDto;
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
	@CacheEvict(value = "posts", key = "#postId")
	public PostDto updatePostStatus(Integer postId, PostStatus newStatus) {
		Post post = postRepo.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post", "Post Id", postId));
		post.setStatus(newStatus);
		postRepo.save(post);

		// Notify the user about the status update
		notificationService.sendPostStatusNotification(post.getUser(), post);
		return this.modelMapper.map(post, PostDto.class);
	}

	@Override
	public void toggleLikePost(Integer postId, Integer userId) {
		Post post = postRepo.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post", "Post Id", postId));
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "User Id", userId));

		Optional<Likes> existingLike = likeRepo.findByPostAndUser(post, user);

		if (existingLike.isPresent()) {
			likeRepo.delete(existingLike.get());
			post.setLikeCount(post.getLikeCount() - 1);
		} else {
			Likes newLike = new Likes();
			newLike.setPost(post);
			newLike.setUser(user);
			newLike.setLikeDate(LocalDateTime.now());
			likeRepo.save(newLike);
			post.setLikeCount(post.getLikeCount() + 1);
		}
		postRepo.save(post);
	}

	@Override
	@Cacheable(value = "posts", key = "#postId")
	public long getPostLikeCount(Integer postId) {
		Post post = postRepo.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post", "Post Id", postId));
		return likeRepo.countByPost(post);
	}

	@Override
	@CacheEvict(value = "posts", key = "{#postId, #post.postCategory.id}")
	public PostDto updatePost(PostDto postDto, Integer postId) {
		Post post = this.postRepo.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post ", "Post Id", postId));
		Category category = categoryRepo.findById(postDto.getPostCategory().getCategoryId()).get();

		String sanitizedContent = contentSanitizer.sanitizeContent(postDto.getPostContent());
		postDto.setPostContent(sanitizedContent);

		if (postDto.getTags() != null) {
			Set<Tag> tags = postDto.getTags().stream().map(tagDto -> {
				Tag tag = tagRepository.findByTagName(tagDto.getTagName()).orElseGet(() -> {
					Tag newTag = new Tag();
					newTag.setTagName(tagDto.getTagName());
					return tagRepository.save(newTag);
				});
				return tag;
			}).collect(Collectors.toSet());
			post.setTags(tags);
		}

		post.setPostTitle(postDto.getPostTitle());
		post.setPostContent(postDto.getPostContent());
		post.setPostImage(postDto.getPostImage());
		post.setPostCategory(category);
		Post updatedPost = this.postRepo.save(post);
		return this.modelMapper.map(updatedPost, PostDto.class);
	}

	@Override
	@CacheEvict(value = "posts", key = "#postId")
	public void deletePost(Integer postId) {
		Post post = this.postRepo.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post ", "Post Id", postId));
		System.out.println("________________________________________________________________");
		post.getComments().clear();
		this.postRepo.delete(post);
	}

	@Override
	public PostResponse getAllPost(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
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
		incrementViewCount(post);
		PostDto postDto = modelMapper.map(post, PostDto.class);
		return postDto;
	}

	@Override
	public void incrementViewCount(Post post) {
		post.setViewCount(post.getViewCount() + 1);
		postRepo.save(post);
	}

	@Override
	@Cacheable(value = "posts", key = "#categoryId")
	public PostResponse getPostByCategory(Integer categoryId, Integer pageNumber, Integer pageSize, String sortBy,
			String sortDir) {
		Category category = this.categoryRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", categoryId));

		Pageable pageable = PaginationUtil.createPageRequest(pageNumber, pageSize, sortBy, sortDir);
		Page<Post> postsByCategoryPage = this.postRepo.findByPostCategory(category, pageable);
		List<Post> postsByCategory = postsByCategoryPage.getContent();

		List<PostListDto> postDtos = postsByCategory.stream().map(post -> this.modelMapper.map(post, PostListDto.class))
				.collect(Collectors.toList());

		PostResponse postResponse = new PostResponse();
		postResponse.setContent(postDtos);
		postResponse.setPageNumber(postsByCategoryPage.getNumber());
		postResponse.setPageSize(postsByCategoryPage.getSize());
		postResponse.setTotalElements(postsByCategoryPage.getTotalElements());
		postResponse.setTotalPages(postsByCategoryPage.getTotalPages());
		postResponse.setFirstPage(postsByCategoryPage.isFirst());
		postResponse.setLastPage(postsByCategoryPage.isLast());

		return postResponse;
	}

	@Override
	@Cacheable(value = "posts", key = "#userId")
	public PostResponse getPostByUser(Integer userId, Integer pageNumber, Integer pageSize, String sortBy,
			String sortDir) {
		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "User Id", userId));

		Pageable pageable = PaginationUtil.createPageRequest(pageNumber, pageSize, sortBy, sortDir);
		Page<Post> postsByUserPage = this.postRepo.findByUser(user, pageable);
		List<Post> postsByUser = postsByUserPage.getContent();

		List<PostListDto> postDtos = postsByUser.stream().map(post -> this.modelMapper.map(post, PostListDto.class))
				.collect(Collectors.toList());

		PostResponse postResponse = new PostResponse();
		postResponse.setContent(postDtos);
		postResponse.setPageNumber(postsByUserPage.getNumber());
		postResponse.setPageSize(postsByUserPage.getSize());
		postResponse.setTotalElements(postsByUserPage.getTotalElements());
		postResponse.setTotalPages(postsByUserPage.getTotalPages());
		postResponse.setFirstPage(postsByUserPage.isFirst());
		postResponse.setLastPage(postsByUserPage.isLast());

		return postResponse;
	}

	@Override
	public PostResponse searchPost(String keyword, Integer pageNumber, Integer pageSize, String sortBy,
			String sortDir) {
		Pageable pageable = PaginationUtil.createPageRequest(pageNumber, pageSize, sortBy, sortDir);
		Page<Post> posts = this.postRepo.findByKeyword(keyword, pageable);
		List<PostListDto> postDtos = posts.stream().map(post -> modelMapper.map(post, PostListDto.class))
				.collect(Collectors.toList());

		PostResponse postResponse = new PostResponse();
		postResponse.setContent(postDtos);
		postResponse.setPageNumber(posts.getNumber());
		postResponse.setPageSize(posts.getSize());
		postResponse.setTotalElements(posts.getTotalElements());
		postResponse.setTotalPages(posts.getTotalPages());
		postResponse.setFirstPage(posts.isFirst());
		postResponse.setLastPage(posts.isLast());

		return postResponse;
	}

	@Override
	@Cacheable(value = "posts", key = "#postId")
	public PostAnalyticsDto getPostAnalytics(Integer postId) {
		Post post = postRepo.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post", "Post Id", postId));

		Long likeCount = post.getLikeCount();
		Long commentCount = commentsRepo.countByPost(post);
		Long viewCount = post.getViewCount();

		// Create the PostAnalyticsDto and populate it with the data
		PostAnalyticsDto analyticsDto = new PostAnalyticsDto();
		analyticsDto.setPostId(postId);
		analyticsDto.setLikeCount(likeCount);
		analyticsDto.setCommentCount(commentCount);
		analyticsDto.setViewCount(viewCount);

		return analyticsDto;
	}

	@Override
	@Cacheable(value = "users", key = "#userId")
	public UserAnalyticsDto getUserAnalytics(Integer userId) {
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "User Id", userId));
		Long postCount = postRepo.countByUser(user);
		Long likeCount = likeRepo.countByUser(user);
		Long commentCount = commentsRepo.countByUser(user);

		UserAnalyticsDto analyticsDto = new UserAnalyticsDto();
		analyticsDto.setUserId(userId);
		analyticsDto.setPostCount(postCount);
		analyticsDto.setLikeCount(likeCount);
		analyticsDto.setCommentCount(commentCount);

		return analyticsDto;
	}

	@Override
	public PostResponse getTrendingPosts(Integer pageNumber, Integer pageSize) {

		int maxTotalElements = 30;
		if (pageSize > 10) {
			pageSize = 10;
		}
		LocalDateTime fifteenDaysAgo = LocalDateTime.now().minusDays(15);
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc("postId")));

		Page<Post> topTrendingPosts = postRepo.findTopTrendingPosts(fifteenDaysAgo, pageable);
		List<PostListDto> postDtos = topTrendingPosts.stream().map(post -> {
			PostListDto postListDto = this.modelMapper.map(post, PostListDto.class);
			postListDto.setUserName(post.getUser().getName());
			postListDto.setCommentsCount(post.getComments().size());
			Long likeCount = post.getLikeCount();
			postListDto.setLikeCount(likeCount);
			return postListDto;
		}).collect(Collectors.toList());

		PostResponse postResponse = new PostResponse();
		postResponse.setContent(postDtos);
		postResponse.setPageNumber(topTrendingPosts.getNumber());
		postResponse.setPageSize(topTrendingPosts.getSize());

		// Limit the total elements to 30
		postResponse.setTotalElements(Math.min(topTrendingPosts.getTotalElements(), maxTotalElements));
		postResponse.setTotalPages((int) Math.ceil((double) postResponse.getTotalElements() / pageSize));

		postResponse.setFirstPage(topTrendingPosts.isFirst());
		postResponse.setLastPage(topTrendingPosts.isLast());

		return postResponse;
	}

	@Override
	@Cacheable(value = "posts", key = "#tagName")
	public PostResponse searchPostsByTag(String tagName, Integer pageNumber, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("postAddedDate").descending());
		Page<Post> postsPage = postRepo.findByTags_TagName(tagName, pageable);
		List<PostListDto> postDtos = postsPage.stream().map(post -> modelMapper.map(post, PostListDto.class))
				.collect(Collectors.toList());

		PostResponse postResponse = new PostResponse();
		postResponse.setContent(postDtos);
		postResponse.setPageNumber(postsPage.getNumber());
		postResponse.setPageSize(postsPage.getSize());
		postResponse.setTotalElements(postsPage.getTotalElements());
		postResponse.setTotalPages(postsPage.getTotalPages());
		postResponse.setFirstPage(postsPage.isFirst());
		postResponse.setLastPage(postsPage.isLast());

		return postResponse;
	}

}
