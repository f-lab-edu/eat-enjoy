package com.restaurant.eatenjoy.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.eatenjoy.dao.RestaurantDao;
import com.restaurant.eatenjoy.dto.PageDto;
import com.restaurant.eatenjoy.dto.RestaurantDto;
import com.restaurant.eatenjoy.dto.RestaurantInfo;
import com.restaurant.eatenjoy.dto.RestaurantListDto;
import com.restaurant.eatenjoy.exception.BizrNoValidException;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.exception.NotFoundException;
import com.restaurant.eatenjoy.exception.RestaurantMinOrderPriceValueException;
import com.restaurant.eatenjoy.util.bizrNoValid.BizrNoValidCheck;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestaurantService {

	private final RestaurantDao restaurantDao;

	@Transactional
	public void register(RestaurantDto restaurantDto, Long ownerId) {

		if (restaurantDto.getPaymentType().equals("선불") && restaurantDto.getMinOrderPrice() == 0) {
			throw new RestaurantMinOrderPriceValueException("매장 결재 방식이 선불일 경우 최소 주문 가격이 0원이 될 순 없습니다");
		}

		if (findByBizrNo(restaurantDto.getBizrNo())) {
			throw new DuplicateValueException("이미 존재하는 사업자 번호입니다");
		}

		if (!BizrNoValidCheck.valid(restaurantDto.getBizrNo())) {
			throw new BizrNoValidException("사업자 등록 번호가 잘못 되었습니다");
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
			.ownerId(ownerId)
			.categoryId(restaurantDto.getCategoryId())
			.openTime(restaurantDto.getOpenTime())
			.closeTime(restaurantDto.getCloseTime())
			.build();

		restaurantDao.register(restaurantDto);
	}

	public boolean findByBizrNo(String bizrNo) {
		return restaurantDao.findByBizrNo(bizrNo);
	}

	public List<RestaurantListDto> getListOfRestaurant(PageDto pageDto) {
		return restaurantDao.findAllRestaurantList(pageDto);
	}

	public RestaurantInfo findById(Long id) {

		if (!isRestaurantInfoExists(id)) {
			throw new NotFoundException("등록되어 있지 않은 식당 입니다");
		}

		return restaurantDao.findById(id);
	}

	public boolean isRestaurantInfoExists(Long id) {
		return restaurantDao.isRestaurantInfoExists(id);
	}
}
