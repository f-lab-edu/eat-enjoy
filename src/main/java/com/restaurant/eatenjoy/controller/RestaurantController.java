package com.restaurant.eatenjoy.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.eatenjoy.annotation.Authority;
import com.restaurant.eatenjoy.annotation.LoginAuthId;
import com.restaurant.eatenjoy.annotation.OwnersRestaurantCheck;
import com.restaurant.eatenjoy.dto.PageDto;
import com.restaurant.eatenjoy.dto.RestaurantDto;
import com.restaurant.eatenjoy.dto.RestaurantInfo;
import com.restaurant.eatenjoy.dto.RestaurantListDto;
import com.restaurant.eatenjoy.service.RestaurantService;
import com.restaurant.eatenjoy.util.security.Role;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
@Authority(Role.OWNER)
public class RestaurantController {

	private final RestaurantService restaurantService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void addRestaurant(@RequestBody @Valid RestaurantDto restaurantDto, @LoginAuthId Long ownerId) {
		restaurantService.register(restaurantDto, ownerId);
	}

	@GetMapping
	public List<RestaurantListDto> getRestaurantList(long lastRestaurantId) {
		return restaurantService.getListOfRestaurant(new PageDto(lastRestaurantId));
	}

	/*
	 * 레스토랑 정보를 조회한다
	 * @param restaurantId 조회할 레스토랑의 id
	 * @param 로그인한 사장님의 id
	 * @return 레스토랑 정보
	 * */
	@GetMapping("{restaurantId}")
	@OwnersRestaurantCheck
	public RestaurantInfo getRestaurant(@PathVariable Long restaurantId) {
		return restaurantService.findById(restaurantId);
	}
}
