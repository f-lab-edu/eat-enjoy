package com.restaurant.eatenjoy.service;

import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.eatenjoy.dao.RestaurantDao;
import com.restaurant.eatenjoy.dto.RestaurantDto;
import com.restaurant.eatenjoy.exception.BizrNoValidException;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.exception.RestaurantMinOrderPriceValueException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestaurantService {

	private final RestaurantDao restaurantDao;

	private static final int[] bizrNoValidKey = new int[] { 1, 3, 7, 1, 3, 7, 1, 3, 5 };

	@Transactional
	public void register(RestaurantDto restaurantDto) {

		if (restaurantDto.getPaymentType().equals("선불") && restaurantDto.getMinOrderPrice() == 0) {
			throw new RestaurantMinOrderPriceValueException("매장 결재 방식이 선불일 경우 최소 주문 가격이 0원이 될 순 없습니다");
		}

		boolean isExistBizrNo = findByBizrNo(restaurantDto.getBizrNo());

		// db에 저장되어있는 사업자 번호와 등록하려는 사업자 번호 비교 존재할 경우 Exception
		if (isExistBizrNo) {
			throw new DuplicateValueException("이미 존재하는 사업자 번호입니다");
		}

		String validBizrNo = validBizrNo(restaurantDto.getBizrNo());
		String endOfBizrNoString = restaurantDto.getBizrNo().substring(restaurantDto.getBizrNo().length() - 1);

		// 사업자 번호 검증 -> Exception
		if (!validBizrNo.equals(endOfBizrNoString)) {
			throw new BizrNoValidException("사업자 등록 번호가 잘못 되었습니다");
		}

		restaurantDao.register(restaurantDto);
	}

	public boolean findByBizrNo(String bizrNo) {
		return restaurantDao.findByBizrNo(bizrNo);
	}

	public String validBizrNo(String bizrNo) {

		bizrNo = bizrNo.replace("-", "");

		if (bizrNo.length() != 10) {
			throw new BizrNoValidException("사업자 등록 번호는 10자리 입니다");
		}

		long chkValue = 0;
		int sum = 0;
		long plusValue = 0;

		long bizrNoToLong = Long.parseLong(bizrNo);

		long [] bizrNoToLongArray = Stream.of(String.valueOf(bizrNoToLong).split("")).mapToLong(Long::parseLong).toArray();

		for (int i = 0; i < bizrNoValidKey.length; i++) {
			// 사업자 번호 9자리와 인증키 9자리를 각각 곱하여 모두 더한다
			sum += bizrNoToLongArray[i] * bizrNoValidKey[i];

			if (i == bizrNoValidKey.length -1) {
				// 사업자 번호 마지막 자리와 인증키의 마지막 자리 값을 곱하고 10으로 나눈다
				plusValue = bizrNoToLongArray[i] * bizrNoValidKey[i] / 10;
			}
		}

		// chkValue값이 사업자 번호 마지막 자리와 같아야지 유효한 사업자 번호
		chkValue = 10 - ((sum + plusValue) % 10);

		return Long.toString(chkValue);
	}
}
