package com.restaurant.eatenjoy.dao;

import java.util.List;

import com.restaurant.eatenjoy.dto.menuGroup.MenuGroupDto;
import com.restaurant.eatenjoy.dto.menuGroup.SimpleMenuGroupInfo;
import com.restaurant.eatenjoy.dto.menuGroup.UpdateMenuGroupDto;

public interface MenuGroupDao {

	List<MenuGroupDto> findAllByRestaurantId(Long restaurantId);

	void register(MenuGroupDto menuGroupDto);

	void updateById(UpdateMenuGroupDto menuGroupDto);

	void deleteById(Long menuGroupId);

	void deleteByRestaurantId(Long restaurantId);

	List<SimpleMenuGroupInfo> findAllAndMenusByRestaurantId(Long restaurantId);

	boolean existsMenusById(Long menuGroupId);
}
