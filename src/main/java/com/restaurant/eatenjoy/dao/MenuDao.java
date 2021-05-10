package com.restaurant.eatenjoy.dao;

import com.restaurant.eatenjoy.dto.MenuDto;
import com.restaurant.eatenjoy.dto.MenuInfo;
import com.restaurant.eatenjoy.dto.UpdateMenuDto;

public interface MenuDao {

	void register(MenuDto menuDto);

	MenuInfo findById(Long menuId);

	void updateById(UpdateMenuDto menuDto);

	void deleteById(Long menuId);

}
