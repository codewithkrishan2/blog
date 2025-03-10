package com.kksg.blog.services.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.kksg.blog.payloads.ImageInfo;
import com.kksg.blog.services.FileService;
import com.kksg.blog.utils.AppConstants;


@Service
public class FileServiceImpl implements FileService {
	
	@Autowired
	private Cloudinary cloudinary;
	

	//Upload Image
	@Override
	public String uploadImage(String path, MultipartFile file) throws IOException {

		//STEP_1: Get Image Original File Name
		
		String name = file.getOriginalFilename();
		
		// Check if the file name is null or empty
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("The file name is invalid.");
		}

		//STEP-2: Random Name Generated fileName using RandomGenerator
		String randomId = UUID.randomUUID().toString();
		
		//Generate a random ID for the file name
		String fileName1 = randomId.concat(name.substring(name.lastIndexOf(".")));
		
		//Step-3: Full Path of the Image
		String filePath = path + File.separator + fileName1;
		
		//STEP-4: creating folder if not created
		
		File f = new File(path);
		//Check if the file exists
		if (!f.exists()) {
			//If not, create the directory
			f.mkdir();
		}

		//STEP-5: File Copy
		
		Files.copy(file.getInputStream(), Path.of(filePath));
		
		return fileName1;
	}

	//Get Resources
	@Override
	public InputStream getResources(String path, String fileName) throws FileNotFoundException {
		
		//Full Path of the Image
		String fullPath = path + File.separator + fileName;
		//Create an InputStream from the fullPath
		InputStream iStream = new FileInputStream(fullPath);
		//Return the InputStream
		return iStream;
	}

	@Override
	public ImageInfo uploadImageToCloudnary(MultipartFile file) throws IOException {
		Map<?, ?> resultMap = cloudinary.uploader()
			.upload(file.getBytes(), ObjectUtils.emptyMap());
		
		return new ImageInfo(
				resultMap.get("public_id").toString(),
				resultMap.get("secure_url").toString(),
				resultMap.get("format").toString()
				);
		
	}

	//serve images from the Cloudnary
	@Override
	public String generateImageUrlFromCloudnary(String publicId) {
		return cloudinary.url().generate(publicId);
	}

	//to serve images in efficient manner from cloudnary
	@Override
	public String generateTransformedImageUrlFromCloudnary(String publicId) {
		return cloudinary.url().transformation(
				new Transformation<>()
						.width(AppConstants.CLOUD_IMAGE_WIDTH)
						.height(AppConstants.CLOUD_IMAGE_HEIGHT)
						.crop(AppConstants.CLOUD_IMAGE_CROP)
				).generate(publicId);
	}
	
}