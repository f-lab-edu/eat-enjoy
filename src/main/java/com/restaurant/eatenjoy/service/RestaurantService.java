package com.restaurant.eatenjoy.service;

import java.util.List;
import java.util.Objects;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.eatenjoy.dao.RestaurantDao;
import com.restaurant.eatenjoy.dto.RestaurantDto;
import com.restaurant.eatenjoy.dto.RestaurantInfo;
import com.restaurant.eatenjoy.dto.RestaurantListDto;
import com.restaurant.eatenjoy.dto.UpdateRestaurant;
import com.restaurant.eatenjoy.exception.BizrNoValidException;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.exception.NotFoundException;
import com.restaurant.eatenjoy.exception.RestaurantMinOrderPriceValueException;
import com.restaurant.eatenjoy.util.BizrNoValidCheck;
import com.restaurant.eatenjoy.util.restaurant.PaymentType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestaurantService {

	private final RestaurantDao restaurantDao;

	@Transactional
	public void register(RestaurantDto restaurantDto, Long ownerId) {

		paymentTypeAndBizrNoValidCheck(restaurantDto.getPaymentType(), restaurantDto.getMinOrderPrice(),
			restaurantDto.getBizrNo());

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

		try {
			restaurantDao.register(restaurantDto);
		} catch (DuplicateKeyException ex) {
			throw new DuplicateValueException("이미 존재하는 사업자 번호입니다", ex);
		}
	}

	public List<RestaurantListDto> getListOfRestaurant(Long lastRestaurantId, Long ownerId) {
		return restaurantDao.findAllRestaurantList(lastRestaurantId, ownerId);
	}

	public RestaurantInfo findById(Long restaurantId) {

		RestaurantInfo restaurantInfo = restaurantDao.findById(restaurantId);

		if (Objects.isNull(restaurantInfo)) {
			throw new NotFoundException("등록되어 있지 않은 식당 입니다");
		}

		return restaurantInfo;
	}

	@Transactional
	public void updateRestaurant(UpdateRestaurant updateRestaurant) {

		paymentTypeAndBizrNoValidCheck(updateRestaurant.getPaymentType(), updateRestaurant.getMinOrderPrice(),
			updateRestaurant.getBizrNo());

		try {
			restaurantDao.modifyRestaurantInfo(updateRestaurant);
		} catch (DuplicateKeyException ex) {
			throw new DuplicateValueException("이미 존재하는 사업자 번호입니다", ex);
		}
	}

	private void paymentTypeAndBizrNoValidCheck(String paymentType, int minOrderPrice, String bizrNo) {

		if ((PaymentType.PREPAYMENT.getPaymentType()).equals(paymentType)
			&& minOrderPrice == 0) {
			throw new RestaurantMinOrderPriceValueException("매장 결제 방식이 선불일 경우 최소 주문 가격이 0원이 될 순 없습니다");
		}

		if (!BizrNoValidCheck.valid(bizrNo)) {
			throw new BizrNoValidException("사업자 등록 번호가 잘못 되었습니다");
		}
	}

}
