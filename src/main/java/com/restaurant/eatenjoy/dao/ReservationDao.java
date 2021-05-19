package com.restaurant.eatenjoy.dao;

import java.util.List;

import com.restaurant.eatenjoy.dto.MenuInfo;
import com.restaurant.eatenjoy.dto.OrderMenuDto;
import com.restaurant.eatenjoy.dto.ReservationDto;

public interface ReservationDao {

	List<MenuInfo> findMenusByOrderMenus(ReservationDto reservationDto);

	void reserve(ReservationDto reservationDto);

	void insertOrderMenus(List<OrderMenuDto> orderMenus);

}
