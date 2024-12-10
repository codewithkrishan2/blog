package com.kksg.blog.payloads;

import lombok.Data;

@Data
public class FlagRequest {

    private Integer userId;
    private String reason;
}
