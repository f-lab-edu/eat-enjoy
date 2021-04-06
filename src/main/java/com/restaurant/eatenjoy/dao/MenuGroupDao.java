package com.restaurant.eatenjoy.dao;

import com.restaurant.eatenjoy.dto.MenuGroupDto;
import com.restaurant.eatenjoy.dto.UpdateMenuGroupDto;

public interface MenuGroupDao {

	void register(MenuGroupDto menuGroupDto);

	void updateById(UpdateMenuGroupDto menuGroupDto);

}
