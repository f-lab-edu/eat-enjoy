package com.restaurant.eatenjoy.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.restaurant.eatenjoy.dao.CategoryDao;
import com.restaurant.eatenjoy.dto.CategoryDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryDao categoryDao;

	public List<CategoryDto> getCategories() {
		return categoryDao.findAll();
	}

}
