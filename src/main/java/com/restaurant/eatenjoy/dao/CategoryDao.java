package com.restaurant.eatenjoy.dao;

import java.util.List;

import com.restaurant.eatenjoy.dto.CategoryDto;

public interface CategoryDao {

	List<CategoryDto> findAll();

}
