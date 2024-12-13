package com.kksg.blog.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kksg.blog.entities.Category;
import com.kksg.blog.exceptions.ApiException;
import com.kksg.blog.exceptions.ResourceNotFoundException;
import com.kksg.blog.payloads.CategoryDto;
import com.kksg.blog.repositories.CategoryRepo;
import com.kksg.blog.services.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepo categoryRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	@Transactional
	public CategoryDto createCategory(CategoryDto categoryDto) {

		Optional<Category> byCategoryTitle = this.categoryRepo.findByCategoryTitle(categoryDto.getCategoryTitle());
		if (byCategoryTitle.isPresent()) {
			throw new ApiException("Category with title " + categoryDto.getCategoryTitle() + " already exists");
		}

		Category parentCategory = null;
		if (categoryDto.getParentCategoryId() != null) {
			parentCategory = categoryRepo.findById(categoryDto.getParentCategoryId())
					.orElseThrow(() -> new ResourceNotFoundException("Category", "Parent Category Id",
							categoryDto.getParentCategoryId()));
		}
		Category category = modelMapper.map(categoryDto, Category.class);
		category.setParentCategory(parentCategory);
		Category savedCategory = this.categoryRepo.save(category);

		return this.modelMapper.map(savedCategory, CategoryDto.class);
	}

	@Override
	@CachePut(value = "categories", key = "#categoryId")
	public CategoryDto updateCategory(CategoryDto categoryDto, Integer categoryId) {
		Category category = this.categoryRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id ", categoryId));

		if (!category.getCategoryTitle().equals(categoryDto.getCategoryTitle())) {
			Optional<Category> byCategoryTitle = this.categoryRepo.findByCategoryTitle(categoryDto.getCategoryTitle());
			if (byCategoryTitle.isPresent()) {
				throw new ApiException("Category with title " + categoryDto.getCategoryTitle() + " already exists");
			}
		}
		if (categoryDto.getParentCategoryId() != null) {
			Category parentCategory = categoryRepo.findById(categoryDto.getParentCategoryId())
					.orElseThrow(() -> new ResourceNotFoundException("Category", "Parent Category Id",
							categoryDto.getParentCategoryId()));
			category.setParentCategory(parentCategory);
		}
		category.setCategoryTitle(categoryDto.getCategoryTitle());
		category.setCategoryDescription(categoryDto.getCategoryDescription());
		Category updatedCategory = this.categoryRepo.save(category);
		return this.modelMapper.map(updatedCategory, CategoryDto.class);
	}

	@Override
	@CacheEvict(value = "categories", key = "#categoryId")
	public void deleteCategory(Integer categoryId) {
		Category category = this.categoryRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category ", "Category Id", categoryId));
		this.categoryRepo.delete(category);

	}

	@Override
	@Cacheable(value = "categories", key = "#categoryId")
	public CategoryDto getCategoryById(Integer categoryId) {
		Category category = this.categoryRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id ", categoryId));
		CategoryDto categoryDto = modelMapper.map(category, CategoryDto.class);
		categoryDto.setSubCategories(category.getSubCategories().stream()
				.map(subCategory -> modelMapper.map(subCategory, CategoryDto.class)).collect(Collectors.toList()));
		return categoryDto;
	}

	@Override
	@Cacheable(value = "categories", key = "'all_categories'")
	public List<CategoryDto> getAllCategory() {
		List<Category> allCategories = this.categoryRepo.findAll();
		return allCategories.stream().map(category -> {
			CategoryDto categoryDto = modelMapper.map(category, CategoryDto.class);
			categoryDto.setSubCategories(category.getSubCategories().stream()
					.map(subCategory -> modelMapper.map(subCategory, CategoryDto.class)).collect(Collectors.toList()));
			return categoryDto;
		}).collect(Collectors.toList());
	}
}
