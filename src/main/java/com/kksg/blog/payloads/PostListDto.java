package com.kksg.blog.payloads;

import java.util.Date;
import lombok.Data;

@Data
public class PostListDto {
	private Integer postId;
	private String postTitle;
	private String postContent;
	private String postImage;
	private Date postAddedDate;
	private CategoryDto postCategory;
	private String userName;
	private Integer commentsCount;
	private String metaTitle;
	private String metaDescription;
	private String metaKeywords;
	private String slug;
	private Integer likeCount;

}
