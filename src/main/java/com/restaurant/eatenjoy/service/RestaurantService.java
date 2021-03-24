package com.restaurant.eatenjoy.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.eatenjoy.dao.RestaurantDao;
import com.restaurant.eatenjoy.dto.RestaurantDto;
import com.restaurant.eatenjoy.exception.BizrNoValidException;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.exception.RestaurantMinOrderPriceValueException;
import com.restaurant.eatenjoy.util.bizrNoValid.BizrNoValidCheck;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestaurantService {

	private final RestaurantDao restaurantDao;

	@Transactional
	public void register(RestaurantDto restaurantDto) {

		if (restaurantDto.getPaymentType().equals("선불") && restaurantDto.getMinOrderPrice() == 0) {
			throw new RestaurantMinOrderPriceValueException("매장 결재 방식이 선불일 경우 최소 주문 가격이 0원이 될 순 없습니다");
		}

		if (findByBizrNo(restaurantDto.getBizrNo())) {
			throw new DuplicateValueException("이미 존재하는 사업자 번호입니다");
		}

		if (!BizrNoValidCheck.validBizrNo(restaurantDto.getBizrNo())) {
			throw new BizrNoValidException("사업자 등록 번호가 잘못 되었습니다");
		}
		restaurantDao.register(restaurantDto);
	}

	public boolean findByBizrNo(String bizrNo) {
		return restaurantDao.findByBizrNo(bizrNo);
	}
}
