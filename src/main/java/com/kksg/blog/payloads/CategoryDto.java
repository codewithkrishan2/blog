package com.kksg.blog.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CategoryDto {

	private Integer categoryId;
	
	@NotBlank
	@Size(min = 3, message = "Category Title Must be min 3 char")
	private String categoryTitle;
	
	@NotBlank
	@Size(min = 20, message = "Description Must be min 20 char")
	private String categoryDescription;
}
