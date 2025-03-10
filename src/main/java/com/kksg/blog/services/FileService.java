package com.kksg.blog.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import com.kksg.blog.payloads.ImageInfo;

public interface FileService {

	String uploadImage(String path, MultipartFile file) throws IOException;
	
	InputStream getResources(String path, String fileName) throws FileNotFoundException;
	
	ImageInfo  uploadImageToCloudnary(MultipartFile file) throws IOException;
	
	String generateImageUrlFromCloudnary( String publicId);

	String generateTransformedImageUrlFromCloudnary(String publicId);
}
