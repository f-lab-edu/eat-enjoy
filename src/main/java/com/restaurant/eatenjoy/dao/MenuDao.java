package com.restaurant.eatenjoy.dao;

import org.apache.ibatis.annotations.Param;

import com.restaurant.eatenjoy.dto.MenuDto;
import com.restaurant.eatenjoy.dto.MenuInfo;
import com.restaurant.eatenjoy.dto.UpdateMenuDto;

public interface MenuDao {

	void register(MenuDto menuDto);

	void updateFileIdById(@Param("menuId") Long menuId, @Param("fileId") Long fileId);

	MenuInfo findById(Long menuId);

	void updateById(UpdateMenuDto menuDto);

	void deleteById(Long menuId);

}
