package com.restaurant.eatenjoy.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.eatenjoy.annotation.Authority;
import com.restaurant.eatenjoy.dto.DayCloseDto;
import com.restaurant.eatenjoy.service.DayCloseService;
import com.restaurant.eatenjoy.util.security.Role;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/day-close")
@RequiredArgsConstructor
@Authority(Role.OWNER)
public class DayCloseController {

	private final DayCloseService dayCloseService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void addDayClose(@RequestBody @Valid DayCloseDto dayCloseDto) {
		dayCloseService.register(dayCloseDto);
	}
}
