package com.kksg.blog.payloads;

import java.util.HashSet;
import java.util.Set;

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
	private String postAddedDate;
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
	
}
