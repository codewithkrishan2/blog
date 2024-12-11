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
import com.kksg.blog.utils.AppConstants;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
	
	@Autowired
	private CategoryService categoryService;

    // Create a new Category
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        try {
            CategoryDto createdCategory = categoryService.createCategory(categoryDto);
            ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, "Category created successfully", createdCategory);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            // Detailed error message
            ApiResponse response = new ApiResponse(AppConstants.FAILED, "Error while creating category: " + e.getMessage(), null, null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update an existing Category
    @PutMapping("/update/{categoryId}")
    public ResponseEntity<ApiResponse> updateCategory(@Valid @RequestBody CategoryDto categoryDto, @PathVariable Integer categoryId) {
        try {
            CategoryDto updatedCategory = categoryService.updateCategory(categoryDto, categoryId);
            ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, "Category updated successfully", updatedCategory);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // Detailed error message
            ApiResponse response = new ApiResponse(AppConstants.FAILED, "Error while updating category: " + e.getMessage(), null, null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete an existing Category by ID
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse> deleteCategoryById(@PathVariable Integer categoryId) {
        try {
            categoryService.deleteCategory(categoryId);
            ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, "Category deleted successfully", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // Detailed error message
            ApiResponse response = new ApiResponse(AppConstants.FAILED, "Error while deleting category: " + e.getMessage(), null, null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Get a Category by ID
    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse> getCategoryById(@PathVariable Integer categoryId) {
        try {
            CategoryDto categoryById = categoryService.getCategoryById(categoryId);
            ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, null, categoryById);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // Detailed error message
            ApiResponse response = new ApiResponse(AppConstants.FAILED, "Category not found: " + e.getMessage(), null, null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    // Get all Categories
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllCategories() {
        try {
            List<CategoryDto> allCategories = categoryService.getAllCategory();
            ApiResponse response = new ApiResponse(AppConstants.SUCCESS, null, null, allCategories);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // Detailed error message
            ApiResponse response = new ApiResponse(AppConstants.FAILED, "Error while fetching categories: " + e.getMessage(), null, null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
