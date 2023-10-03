package com.kksg.blog.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kksg.blog.payloads.ApiResponse;
import com.kksg.blog.payloads.CategoryDto;
import com.kksg.blog.services.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/category/")
public class CategoryController {
	
	@Autowired
	private CategoryService categoryService;

	//Create Category
	//Update Category
	//Delete Category
	//Get Category
	//Get All Categories
	
    @PostMapping("/create")
	public ResponseEntity<CategoryDto> createCategory(@Valid
			@RequestBody CategoryDto categoryDto){
		CategoryDto createdCategory = this.categoryService.createCategory(
				categoryDto);
		return new ResponseEntity<CategoryDto>(
				createdCategory, HttpStatus.CREATED);
	}
    
    @PutMapping("/update/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@Valid
    		@RequestBody CategoryDto categoryDto,
    		@PathVariable Integer categoryId){
    	CategoryDto updatedCategory = this.categoryService.updateCategory(categoryDto, categoryId);
    	return new ResponseEntity<CategoryDto>(
    			updatedCategory, HttpStatus.OK);
    }
    
    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<ApiResponse> deleteCategoryById(@PathVariable Integer categoryId){
    	this.categoryService.deleteCategory(categoryId);
    	return new ResponseEntity<ApiResponse> ( 
    			 new ApiResponse("Successfully deleted", true),
    			 HttpStatus.OK);    	
    }
    
    @GetMapping("/one/{categoryId}")
    public ResponseEntity<?> getCategoryById(@PathVariable Integer categoryId){
    	CategoryDto categoryById = this.categoryService.getCategoryById(categoryId);
    	return new ResponseEntity<CategoryDto>(categoryById, HttpStatus.OK);
    }
    
    @GetMapping("/allCategory")
    public ResponseEntity<?> getAllCategories(){
    	List<CategoryDto> allCategory = this.categoryService.getAllCategory();
    	return new ResponseEntity<List<CategoryDto>>(allCategory, HttpStatus.OK);
    }
}
