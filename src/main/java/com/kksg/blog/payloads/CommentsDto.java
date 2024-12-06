package com.kksg.blog.payloads;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentsDto {


	private Integer commentId;

	private String content;

	private Integer userId;
	
	private String username;
	
	private Integer postId;
	
	private String createdAt;
	
	
	@JsonIgnore
	public Integer getPostId() {
		return postId;
	}
	
	@JsonProperty
	public void setPostId(Integer postId) {
		this.postId = postId;
	}
	
	@JsonIgnore
	public Integer getUserId() {
		return userId;
	}
	
	@JsonProperty
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	

}
