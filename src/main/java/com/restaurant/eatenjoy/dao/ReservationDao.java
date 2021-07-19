package com.restaurant.eatenjoy.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.restaurant.eatenjoy.dto.menu.MenuInfo;
import com.restaurant.eatenjoy.dto.reservation.OrderMenuDto;
import com.restaurant.eatenjoy.dto.reservation.ReservationDto;
import com.restaurant.eatenjoy.dto.reservation.ReservationInfo;
import com.restaurant.eatenjoy.dto.reservation.ReservationSearchDto;
import com.restaurant.eatenjoy.dto.reservation.SimpleReservationDto;
import com.restaurant.eatenjoy.util.type.ReservationStatus;

public interface ReservationDao {

	List<MenuInfo> findMenusByOrderMenus(ReservationDto reservationDto);

	List<SimpleReservationDto> findAllReservation(ReservationSearchDto reservationSearchDto);

	ReservationInfo findReservation(ReservationSearchDto reservationSearchDto);

	ReservationInfo findByIdAndUserId(@Param("id") Long reservationId, @Param("userId") Long userId);

	void reserve(ReservationDto reservationDto);

	void insertOrderMenus(List<OrderMenuDto> orderMenus);

	void deleteById(Long reservationId);

	void updateStatusById(@Param("id") Long reservationId, @Param("status") ReservationStatus status);

}
