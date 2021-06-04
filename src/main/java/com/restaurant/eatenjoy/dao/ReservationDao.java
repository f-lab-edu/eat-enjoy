package com.restaurant.eatenjoy.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.restaurant.eatenjoy.dto.MenuInfo;
import com.restaurant.eatenjoy.dto.OrderMenuDto;
import com.restaurant.eatenjoy.dto.ReservationDto;
import com.restaurant.eatenjoy.dto.ReservationInfo;
import com.restaurant.eatenjoy.util.type.ReservationStatus;

public interface ReservationDao {

	List<MenuInfo> findMenusByOrderMenus(ReservationDto reservationDto);

	ReservationInfo findByIdAndUserId(@Param("id") Long reservationId, @Param("userId") Long userId);

	void reserve(ReservationDto reservationDto);

	void insertOrderMenus(List<OrderMenuDto> orderMenus);

	void deleteById(Long reservationId);

	void updateStatusById(@Param("id") Long reservationId, @Param("status") ReservationStatus status);

}
