package com.restaurant.eatenjoy.dao;

import java.util.List;

import com.restaurant.eatenjoy.dto.MenuGroupDto;
import com.restaurant.eatenjoy.dto.UpdateMenuGroupDto;

public interface MenuGroupDao {

	List<MenuGroupDto> findAllByRestaurantId(Long restaurantId);

	void register(MenuGroupDto menuGroupDto);

	int updateById(UpdateMenuGroupDto menuGroupDto);

	void deleteById(Long menuGroupId);

}
