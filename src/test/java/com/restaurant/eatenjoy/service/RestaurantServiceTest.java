package com.restaurant.eatenjoy.service;

import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.restaurant.eatenjoy.controller.RestaurantController;
import com.restaurant.eatenjoy.dto.RestaurantDto;

@AutoConfigureMockMvc
@SpringBootTest
class RestaurantServiceTest {

	@Autowired
	RestaurantController restaurantController;

	RestaurantDto restaurantDto;

	@Test
	@DisplayName("매장 계산 식당 등록")
	void registerToStorePaymentRestaurant() {

		restaurantDto = RestaurantDto.builder()
			.name("청기와")
			.bizrNo("1234567890")
			.address("수원시")
			.regionCd("cod")
			.telNo("02-123-4567")
			.intrDc("청기와 소개글")
			.paymentType("매장 결재")
			.ownerId(1L)
			.categoryId(1L)
			.openTime(LocalTime.of(9, 00))
			.closeTime(LocalTime.of(23, 00))
			.build();

		restaurantController.addRestaurant(restaurantDto);
	}

	@Test
	@DisplayName("선불 계산 식당 등록")
	void registerToPrepaymentRestaurant() {

		restaurantDto = RestaurantDto.builder()
			.name("청기와")
			.bizrNo("1111111111")
			.address("수원시")
			.regionCd("cod")
			.telNo("02-123-4567")
			.intrDc("청기와 소개글")
			.paymentType("선불")
			.minOrderPrice(7000)
			.ownerId(1L)
			.categoryId(1L)
			.openTime(LocalTime.of(9, 00))
			.closeTime(LocalTime.of(23, 00))
			.build();

		restaurantController.addRestaurant(restaurantDto);
	}

	@Test
	@DisplayName("선불 계산 식당 등록 시 최소 주문 가격이 0원이 되면 Exception 발생")
	void failToRegisterRestaurantByMinOrderPrice() {
		restaurantDto = RestaurantDto.builder()
			.name("청기와")
			.bizrNo("2222222222")
			.address("수원시")
			.regionCd("cod")
			.telNo("02-123-4567")
			.intrDc("청기와 소개글")
			.paymentType("선불")
			.ownerId(1L)
			.categoryId(1L)
			.openTime(LocalTime.of(9, 00))
			.closeTime(LocalTime.of(23, 00))
			.build();

		restaurantController.addRestaurant(restaurantDto);
	}

}
