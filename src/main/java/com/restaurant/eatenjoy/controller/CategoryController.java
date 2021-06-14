package com.restaurant.eatenjoy.controller;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.eatenjoy.annotation.Authority;
import com.restaurant.eatenjoy.dto.category.CategoryDto;
import com.restaurant.eatenjoy.dto.restaurant.SimpleRestaurantDto;
import com.restaurant.eatenjoy.service.CategoryService;
import com.restaurant.eatenjoy.util.cache.CacheNames;
import com.restaurant.eatenjoy.util.security.Role;

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

	@Authority(Role.USER)
	@GetMapping("/{categoryId}/restaurants")
	public List<SimpleRestaurantDto> restaurants(@PathVariable Long categoryId, String sigunguCd, Long lastRestaurantId) {
		return categoryService.getRestaurantsBy(categoryId, sigunguCd, lastRestaurantId);
	}

}
