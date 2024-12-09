package com.kksg.blog.payloads;

import lombok.Data;

@Data
public class UserAnalyticsDto {

    private Integer userId;
    private Long postCount;
    private Long likeCount;
    private Long commentCount;

}
