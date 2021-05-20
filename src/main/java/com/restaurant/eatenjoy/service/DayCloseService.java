package com.restaurant.eatenjoy.service;

import java.time.LocalDate;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.restaurant.eatenjoy.dao.DayCloseDao;
import com.restaurant.eatenjoy.dto.DayCloseDto;
import com.restaurant.eatenjoy.exception.DuplicateValueException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DayCloseService {

	private final DayCloseDao dayCloseDao;

	public void register(DayCloseDto dayCloseDto) {
		try {
			dayCloseDao.register(dayCloseDto);
		} catch (DuplicateKeyException ex) {
			throw new DuplicateValueException("이미 마감 처리된 식당 입니다", ex);
		}
	}

	public boolean isRestaurantDayClose(Long restaurantId, LocalDate closeDate) {
		return dayCloseDao.existsByRestaurantIdAndCloseDate(restaurantId, closeDate);
	}
}
