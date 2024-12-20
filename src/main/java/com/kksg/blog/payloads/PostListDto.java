package com.kksg.blog.payloads;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.Data;

@Data
public class PostListDto {
	private Integer postId;
	private String postTitle;
	private String postContent;
	private String postImage;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
	private Boolean isDeleted;
	private CategoryDto postCategory;
	private String userName;
	private Integer commentsCount;
	private String metaTitle;
	private String metaDescription;
	private String metaKeywords;
	private String slug;
    private long viewCount; 
    private long likeCount;
    private Set<TagDto> tags;
}
