package com.kksg.blog.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationUtil {

	 public static Pageable createPageRequest(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
	        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
	        return PageRequest.of(pageNumber, pageSize, sort);
	    }
}
