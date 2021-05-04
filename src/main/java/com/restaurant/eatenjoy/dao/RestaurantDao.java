package com.restaurant.eatenjoy.dao;

import java.util.List;

import com.restaurant.eatenjoy.dto.RestaurantDto;
import com.restaurant.eatenjoy.dto.RestaurantInfo;
import com.restaurant.eatenjoy.dto.RestaurantListDto;
import com.restaurant.eatenjoy.dto.UpdateRestaurant;

public interface RestaurantDao {

	void register(RestaurantDto restaurantDto);

	boolean existByBizrNo(String bizrNo);

	List<RestaurantListDto> findAllRestaurantList(Long lastRestaurantId, Long ownerId);

	RestaurantInfo findById(Long id);

	void modifyRestaurantInfo(UpdateRestaurant updateRestaurant);
}
