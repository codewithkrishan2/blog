package com.kksg.blog.services;

import com.kksg.blog.entities.Post;
import com.kksg.blog.entities.User;

public interface NotificationService {

	void sendPostStatusNotification(User user, Post post);

}
