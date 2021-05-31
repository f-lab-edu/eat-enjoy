package com.restaurant.eatenjoy.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.restaurant.eatenjoy.dto.MenuDto;
import com.restaurant.eatenjoy.dto.MenuInfo;
import com.restaurant.eatenjoy.dto.SimpleMenuGroupInfo;
import com.restaurant.eatenjoy.dto.UpdateMenuDto;

public interface MenuDao {

	boolean existsByRestaurantIdAndName(@Param("restaurantId") Long restaurantId,
		@Param("menuId") Long menuId, @Param("name") String name);

	void register(MenuDto menuDto);

	MenuInfo findById(Long menuId);

	void updateById(UpdateMenuDto menuDto);

	void deleteById(Long menuId);

	void deleteByIdIn(List<SimpleMenuGroupInfo.MenuInfo> menus);

}
