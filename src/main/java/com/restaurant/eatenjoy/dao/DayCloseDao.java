package com.restaurant.eatenjoy.dao;

import java.time.LocalDate;

import org.apache.ibatis.annotations.Param;

import com.restaurant.eatenjoy.dto.DayCloseDto;

public interface DayCloseDao {

	void register(DayCloseDto dayCloseDto);

	boolean existsByRestaurantIdAndCloseDate(@Param("restaurantId") Long restaurantId, @Param("closeDate") LocalDate closeDate);
}
