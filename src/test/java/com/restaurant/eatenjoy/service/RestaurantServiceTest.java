package com.restaurant.eatenjoy.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.restaurant.eatenjoy.dao.RestaurantDao;
import com.restaurant.eatenjoy.dto.PageDto;
import com.restaurant.eatenjoy.dto.RestaurantDto;
import com.restaurant.eatenjoy.dto.RestaurantListDto;
import com.restaurant.eatenjoy.exception.BizrNoValidException;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.exception.RestaurantMinOrderPriceValueException;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

	public static final Long OWNER_ID = 1L;

	@Mock
	private RestaurantDao restaurantDao;

	@InjectMocks
	private RestaurantService restaurantService;

	private RestaurantDto duplicatedBizrNoRestaurantDto() {
		RestaurantDto restaurantDto	= RestaurantDto.builder()
			.name("청기와")
			.bizrNo("1234567891")
			.address("수원시")
			.regionCd("cod")
			.telNo("02-123-4567")
			.intrDc("청기와 소개글")
			.paymentType("매장 결재")
			.ownerId(OWNER_ID)
			.categoryId(1L)
			.openTime(LocalTime.of(9,00))
			.closeTime(LocalTime.of(23, 00))
			.build();

		return restaurantDto;
	}

	private RestaurantDto paymentTypeRestaurantDto() {
		RestaurantDto restaurantDto = RestaurantDto.builder()
			.name("청기와")
			.bizrNo("1234567891")
			.address("수원시")
			.regionCd("cod")
			.telNo("02-123-4567")
			.intrDc("청기와 소개글")
			.paymentType("선불")
			.ownerId(OWNER_ID)
			.categoryId(1L)
			.openTime(LocalTime.of(9,00))
			.closeTime(LocalTime.of(23, 00))
			.build();

		return restaurantDto;
	}

	private RestaurantDto notExistBizrNoRestaurntDto() {
		RestaurantDto restaurantDto = RestaurantDto.builder()
			.name("청기와")
			.bizrNo("1234567892")
			.address("수원시")
			.regionCd("cod")
			.telNo("02-123-4567")
			.intrDc("청기와 소개글")
			.paymentType("매장 결재")
			.ownerId(OWNER_ID)
			.categoryId(1L)
			.openTime(LocalTime.of(9,00))
			.closeTime(LocalTime.of(23, 00))
			.build();

		return restaurantDto;
	}

	private RestaurantDto successRestaurantDto() {
		RestaurantDto restaurantDto = RestaurantDto.builder()
			.name("청기와")
			.bizrNo("1234567891")
			.address("수원시")
			.regionCd("cod")
			.telNo("02-123-4567")
			.intrDc("청기와 소개글")
			.paymentType("매장 결재")
			.ownerId(OWNER_ID)
			.categoryId(1L)
			.openTime(LocalTime.of(9,00))
			.closeTime(LocalTime.of(23, 00))
			.build();

		return restaurantDto;
	}

	private RestaurantListDto createRestaurantData(long id, String name, String intrDc) {
		RestaurantListDto restaurantListDto = RestaurantListDto.builder()
			.id(id)
			.name(name)
			.intrdc(intrDc)
			.build();

		return restaurantListDto;
	}

	@Test
	@DisplayName("매장 방식이 선불인 경우 최소 주문 가격이 0원이 될 순 없다")
	void failToMinOrderPriceByPaymentType() {
		assertThrows(RestaurantMinOrderPriceValueException.class, () -> {
			restaurantService.register(paymentTypeRestaurantDto(), OWNER_ID);
		});
	}

	@Test
	@DisplayName("중복된 사업자 번호는 식당 등록을 할 수 없다")
	void failToRegisterRestaurantToBizrNoDuplicated() {
		given(restaurantDao.findByBizrNo("1234567891")).willReturn(true);
		assertThatThrownBy(() -> restaurantService.register(duplicatedBizrNoRestaurantDto(),OWNER_ID)).isInstanceOf(
			DuplicateValueException.class);
	}

	@Test
	@DisplayName("존재하지 않는 사업자 등록번호를 입력하면 식당 등록을 할 수 없다")
	void failToRegisterRestaurantByNotExistsBizrNo() {
		assertThrows(BizrNoValidException.class, () -> {
			restaurantService.register(notExistBizrNoRestaurntDto(), OWNER_ID);
		});
	}

	@Test
	@DisplayName("사업자 번호가 중복되지 않는다면 식당 등록을 성공한다")
	void successToRegisterRestaurant() {
		given(restaurantDao.findByBizrNo(successRestaurantDto().getBizrNo())).willReturn(false);

		restaurantService.register(successRestaurantDto(), OWNER_ID);

		then(restaurantDao).should(times(1)).findByBizrNo(successRestaurantDto().getBizrNo());
		then(restaurantDao).should(times(1)).register(any(RestaurantDto.class));
	}

	@Test
	@DisplayName("식당 목록 데이터가 있는 경우 리스트를 반환한다")
	void getExistRestaurntList() {
		// given
		List<RestaurantListDto> existRestaurantList = new ArrayList<>();
		existRestaurantList.add(createRestaurantData(1L, "김밥나라", "김밥나라 소개글"));
		existRestaurantList.add(createRestaurantData(1L, "맥도날드", "맥도날드 소개글"));
		
		PageDto pageDto = new PageDto(0);

		when(restaurantDao.findAllRestaurantList(pageDto)).thenReturn(existRestaurantList);

		// when
		List<RestaurantListDto> result = restaurantService.getListOfRestaurant(pageDto);

		// then
		assertEquals(existRestaurantList, result);
	}

	@Test
	@DisplayName("식당 목록 데이터가 없는 경우 비어있는 리스트를 리턴한다")
	void getEmptyRestaurantList() {
		// given
		List<RestaurantListDto> emptyRestaurantList = new ArrayList<>();
		PageDto pageDto = new PageDto(0);

		when(restaurantDao.findAllRestaurantList(pageDto)).thenReturn(emptyRestaurantList);

		// when
		List<RestaurantListDto> result = restaurantService.getListOfRestaurant(pageDto);

		// then
		assertEquals(emptyRestaurantList, result);
	}
}
