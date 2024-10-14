package com.kksg.blog.services;

import org.springframework.mail.SimpleMailMessage;

public interface EmailService {

	void sendEmail(SimpleMailMessage email);

	void sendOtpEmail(String to, int otp);
	
	int generateOtp();
}
