package com.restaurant.eatenjoy.controller;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.eatenjoy.dto.CategoryDto;
import com.restaurant.eatenjoy.service.CategoryService;
import com.restaurant.eatenjoy.util.cache.CacheNames;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;

	@Cacheable(value = CacheNames.CATEGORY, cacheManager = CacheNames.SIMPLE_CACHE_MANAGER)
	@GetMapping
	public List<CategoryDto> categories() {
		return categoryService.getCategories();
	}

}
