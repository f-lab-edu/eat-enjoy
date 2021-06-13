package com.restaurant.eatenjoy.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.restaurant.eatenjoy.dao.CategoryDao;
import com.restaurant.eatenjoy.dto.category.CategoryDto;
import com.restaurant.eatenjoy.dto.restaurant.SimpleRestaurantDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryDao categoryDao;

	public List<CategoryDto> getCategories() {
		return categoryDao.findAll();
	}

	public List<SimpleRestaurantDto> getRestaurantsBy(Long categoryId, String sigunguCd, Long lastRestaurantId) {
		return categoryDao.findRestaurantsBy(categoryId, sigunguCd, lastRestaurantId);
	}

}
