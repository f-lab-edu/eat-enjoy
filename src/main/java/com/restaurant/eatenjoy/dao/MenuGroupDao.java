package com.restaurant.eatenjoy.dao;

import com.restaurant.eatenjoy.dto.MenuGroupDto;

public interface MenuGroupDao {

	boolean existsByName(String name);

	void register(MenuGroupDto menuGroupDto);

}
