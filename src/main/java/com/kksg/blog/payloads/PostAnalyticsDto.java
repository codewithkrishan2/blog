package com.kksg.blog.payloads;

import lombok.Data;

@Data
public class PostAnalyticsDto {
	private Integer postId;
	private Long likeCount;
	private Long commentCount;
	private Long viewCount;
}
