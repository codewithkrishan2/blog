package com.kksg.blog.payloads;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.kksg.blog.entities.enums.PostStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

	private Integer postId;
	
	@NotBlank(message = "Post Title is mandatory")
	@Size(min = 6, message = "Post Title must be min - 6 characters")
	private String postTitle;
	private String postContent;
	private String postImage;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
	private Boolean isDeleted;
	private CategoryDto postCategory;
	private UserDto user;
	private Set<CommentsDto> comments = new HashSet<>();
	// SEO Fields
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;
    private String slug;
    
    private long viewCount; 
    private long likeCount;
    private Set<TagDto> tags;
    
    private PostStatus status;
	
}
