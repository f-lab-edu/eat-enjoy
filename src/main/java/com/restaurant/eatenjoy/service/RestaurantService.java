package com.restaurant.eatenjoy.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.eatenjoy.dao.RestaurantDao;
import com.restaurant.eatenjoy.dto.RestaurantDto;
import com.restaurant.eatenjoy.exception.RestaurantMinOrderPriceValueException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestaurantService {

	private final RestaurantDao restaurantDao;

	@Transactional
	public void register(RestaurantDto restaurantDto) {

		if (restaurantDto.getPaymentType() == "선불" && restaurantDto.getMinOrderPrice() == 0) {

			throw new RestaurantMinOrderPriceValueException("매장 결재 방식이 선불일 경우 최소 주문 가격이 0원이 될 순 없습니다");
		}

		restaurantDto = RestaurantDto.builder()
			.name(restaurantDto.getName())
			.bizrNo(restaurantDto.getBizrNo())
			.address(restaurantDto.getAddress())
			.regionCd(restaurantDto.getRegionCd())
			.telNo(restaurantDto.getTelNo())
			.intrDc(restaurantDto.getIntrDc())
			.minOrderPrice(restaurantDto.getMinOrderPrice())
			.paymentType(restaurantDto.getPaymentType())
			.ownerId(restaurantDto.getOwnerId())
			.categoryId(restaurantDto.getCategoryId())
			.openTime(restaurantDto.getOpenTime())
			.closeTime(restaurantDto.getCloseTime())
			.build();

		restaurantDao.register(restaurantDto);
	}
}
