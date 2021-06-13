package com.restaurant.eatenjoy.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.restaurant.eatenjoy.dto.menu.MenuDto;
import com.restaurant.eatenjoy.dto.menu.MenuInfo;
import com.restaurant.eatenjoy.dto.menu.UpdateMenuDto;

public interface MenuDao {

	boolean existsByRestaurantIdAndName(@Param("restaurantId") Long restaurantId,
		@Param("menuId") Long menuId, @Param("name") String name);

	void register(MenuDto menuDto);

	MenuInfo findById(Long menuId);

	List<MenuInfo> findAllByRestaurantId(Long restaurantId);

	void updateById(UpdateMenuDto menuDto);

	void deleteById(Long menuId);

	void deleteByIdIn(List<MenuInfo> menus);
}
