package com.kksg.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Configuration
public class CloudnaryConfig {

	@Value("${config.cloudinary.cloud.key}")
	private String key;
	
	@Value("${config.cloudinary.cloud.name}")
	private String cloudName;
	
	@Value("${config.cloudinary.cloud.secret}")
	private String secret;
	
	
	@Bean
	Cloudinary cloudinary() {
		return new Cloudinary(
				
				ObjectUtils.asMap(
						"cloud_name", cloudName,
						"api_key", key,
						"api_secret", secret
				)
		);
	}
	
}
