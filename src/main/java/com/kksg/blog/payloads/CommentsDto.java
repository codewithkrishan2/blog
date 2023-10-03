package com.kksg.blog.payloads;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentsDto {

	private Integer commentId;
	
	private String content;
	
}
