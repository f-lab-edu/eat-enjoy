package com.restaurant.eatenjoy.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.eatenjoy.annotation.Authority;
import com.restaurant.eatenjoy.annotation.LoginAuthId;
import com.restaurant.eatenjoy.dto.PageDto;
import com.restaurant.eatenjoy.dto.RestaurantDto;
import com.restaurant.eatenjoy.dto.RestaurantListDto;
import com.restaurant.eatenjoy.service.RestaurantService;
import com.restaurant.eatenjoy.util.security.Role;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

	private final RestaurantService restaurantService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Authority(Role.OWNER)
	public void addRestaurant(@RequestBody @Valid RestaurantDto restaurantDto, @LoginAuthId Long ownerId) {
		restaurantService.register(restaurantDto, ownerId);
	}

	@GetMapping
	public ResponseEntity<List<RestaurantListDto>> getRestaurantList(long lastRestaurantId) {
		List<RestaurantListDto> list = restaurantService.getListOfRestaurant(new PageDto(lastRestaurantId));
		return ResponseEntity.ok().body(list);
	}
}
