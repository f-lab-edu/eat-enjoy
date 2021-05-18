package com.restaurant.eatenjoy.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.restaurant.eatenjoy.dto.CategoryDto;
import com.restaurant.eatenjoy.dto.SimpleRestaurantDto;

public interface CategoryDao {

	List<CategoryDto> findAll();

	List<SimpleRestaurantDto> findRestaurantsBy(@Param("categoryId") Long categoryId, @Param("sigunguCd") String sigunguCd,
		@Param("lastRestaurantId") Long lastRestaurantId);

}
