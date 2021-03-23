package com.restaurant.eatenjoy.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.eatenjoy.dto.RestaurantDto;
import com.restaurant.eatenjoy.service.RestaurantService;

@AutoConfigureMockMvc
@SpringBootTest
class RestaurantControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	WebApplicationContext ctx;

	@Autowired
	RestaurantService restaurantService;

	RestaurantDto restaurantDto;

	@BeforeEach
	void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
			.addFilter(new CharacterEncodingFilter("UTF-8", true))
			.alwaysDo(print())
			.build();
	}

	@Test
	@DisplayName("매장 계산 식당 등록")
	void registerToStorePaymentRestaurant() throws Exception {
		restaurantDto = RestaurantDto.builder()
			.name("청기와")
			.bizrNo("1234567891")
			.address("수원시")
			.regionCd("cod")
			.telNo("02-123-4567")
			.intrDc("청기와 소개글")
			.paymentType("매장 결재")
			.ownerId(1L)
			.categoryId(1L)
			.openTime(LocalTime.of(9,00))
			.closeTime(LocalTime.of(23, 00))
			.build();

		String json = objectMapper.writeValueAsString(restaurantDto);

		this.mockMvc.perform(post("/api/restaurants")
			.contentType(MediaType.APPLICATION_JSON)
			.content(json))
			.andDo(print())
			.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("잘못된 시간 지정으로 인한 등록 실패")
	void shouldNotAcceptWrongTime() throws Exception {
		restaurantDto = RestaurantDto.builder()
			.name("청기와")
			.bizrNo("1234567891")
			.address("수원시")
			.regionCd("cod")
			.telNo("02-123-4567")
			.intrDc("청기와 소개글")
			.paymentType("매장 결재")
			.ownerId(1L)
			.categoryId(1L)
			.openTime(LocalTime.of(25,00))
			.closeTime(LocalTime.of(23, 00))
			.build();

		String json = objectMapper.writeValueAsString(restaurantDto);

		this.mockMvc.perform(post("/api/restaurants")
			.contentType(MediaType.APPLICATION_JSON)
			.content(json))
			.andDo(print())
			.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("사업자 번호 10자리 미만 입력으로 인한 등록 실패")
	void shouldNotAcceptWrongBizrNo() throws Exception {
		restaurantDto = RestaurantDto.builder()
			.name("청기와")
			.bizrNo("1234")
			.address("수원시")
			.regionCd("cod")
			.telNo("02-123-4567")
			.intrDc("청기와 소개글")
			.paymentType("매장 결재")
			.ownerId(1L)
			.categoryId(1L)
			.openTime(LocalTime.of(9,00))
			.closeTime(LocalTime.of(23, 00))
			.build();

		String json = objectMapper.writeValueAsString(restaurantDto);

		this.mockMvc.perform(post("/api/restaurants")
			.contentType(MediaType.APPLICATION_JSON)
			.content(json))
			.andDo(print())
			.andExpect(status().isCreated());
	}
}
