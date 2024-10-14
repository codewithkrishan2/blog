package com.kksg.blog.services.impl;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.kksg.blog.services.EmailService;

public class EmailServiceImpl implements EmailService {

	@Autowired
	private JavaMailSender mailSender; 
	
	@Override
	public void sendEmail(SimpleMailMessage email) {
		mailSender.send(email);
	}

	@Override
	public void sendOtpEmail(String to, int otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("krishankantsinghtesting@gmail.com");
        message.setTo(to);
        message.setSubject("Your OTP Code");
        message.setText("To login to your account, Your OTP code is: " + otp);
        mailSender.send(message);
    }

	@Override
	public int generateOtp() {
		Random random = new Random();
        int otp = random.nextInt(900000) + 100000;
        return otp;
		
	}

}
