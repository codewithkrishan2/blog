package com.kksg.blog.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.kksg.blog.entities.FlaggedComment;
import com.kksg.blog.entities.Post;
import com.kksg.blog.entities.User;
import com.kksg.blog.entities.enums.PostStatus;
import com.kksg.blog.services.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

	@Autowired
    private JavaMailSender emailSender;

	private static final String adminEmail = "kksg1999@gmail.com";
	
	@Override
    public void sendPostStatusNotification(User user, Post post) {
		
		String rejectSubject = "Your post '" + post.getPostTitle() + "' has been rejected.";
		String approveSubject = "Your post '" + post.getPostTitle() + "' has been approved.";
		String pendingSubject = "Your post '" + post.getPostTitle() + "' is Pending.";
		
		String rejectMessage = "Hello " + user.getName() + ",\n\n" +
							   "Your post titled '" + post.getPostTitle() + "' has been rejected. \n" +
							   "Post Status: " + post.getStatus() + "\n\n" +
							   "Thank you for contributing!";
		String approveMessage = "Hello " + user.getName() + ",\n\n" +
								"Your post titled '" + post.getPostTitle() + "' has been approved. \n" +
								"Post Status: " + post.getStatus() + "\n\n" +
								"Thank you for contributing!";
		String pendingMessage = "Hello " + user.getName() + ",\n\n" +
								"Your post titled '" + post.getPostTitle() + "' is currently under review. \n" +
								"Post Status: " + post.getStatus() + "\n\n" +
								"Thank you for contributing!";
		
        // Create the email content
        String subject = null;
        String message = null;
        
        if (post.getStatus().equals(PostStatus.PUBLISHED)) {
			subject = approveSubject;
			message = approveMessage;
		} else if (post.getStatus().equals(PostStatus.REJECTED)) {
			subject = rejectSubject;
			message = rejectMessage;
		} else {
			subject = pendingSubject;
			message = pendingMessage;
		}
        
        // Create and send the email
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setCc("kksg1999@gmail.com");
        email.setSubject(subject);
        email.setText(message);
        emailSender.send(email);
    }
	
	// Send email notification for flagged comment
	@Override
    public void sendFlaggedCommentNotification(FlaggedComment flaggedComment) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(adminEmail);
        message.setSubject("New Flagged Comment Notification");
        message.setText("A comment has been flagged for review:\n\n" +
                        "Comment ID: " + flaggedComment.getComment().getCommentId() + "\n" +
                        "Flagged by: " + flaggedComment.getUser().getUsername() + "\n" +
                        "Reason: " + flaggedComment.getReason() + "\n\n" +
                        "Comment content: " + flaggedComment.getComment().getContent());

        // Send the email
        emailSender.send(message);
    }

    // Send email notification for offensive content (detected by content filtering)
	@Override
    public void sendOffensiveContentNotification(String commentContent) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(adminEmail);
        message.setSubject("Offensive Content Detected");
        message.setText("The following comment contains offensive content and was rejected:\n\n" +
                        "Content: " + commentContent);

        // Send the email
        emailSender.send(message);
    }
}
