package com.kksg.blog.payloads;

import lombok.Data;

@Data
public class OtpRequest {
    private String username;
    private int otp;
}
