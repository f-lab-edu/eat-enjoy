package com.restaurant.eatenjoy.dao;

import java.util.List;

import com.restaurant.eatenjoy.dto.restaurant.RestaurantDto;
import com.restaurant.eatenjoy.dto.restaurant.RestaurantInfo;
import com.restaurant.eatenjoy.dto.restaurant.RestaurantListDto;
import com.restaurant.eatenjoy.dto.restaurant.UpdateRestaurantDto;

public interface RestaurantDao {

	void register(RestaurantDto restaurantDto);

	List<RestaurantListDto> findAllRestaurantList(Long lastRestaurantId, Long ownerId);

	RestaurantInfo findById(Long id);

	void modifyRestaurantInfo(UpdateRestaurantDto updateRestaurantDto);

	void deleteById(Long id);

}
