package com.kksg.blog.services.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kksg.blog.services.FileService;


@Service
public class FileServiceImpl implements FileService {

	//Upload Image
	@Override
	public String uploadImage(String path, MultipartFile file) throws IOException {

		//STEP_1: Get Image Original File Name
		
		String name = file.getOriginalFilename();
		
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
		
		Files.copy(file.getInputStream(), Paths.get(filePath));
		
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

}