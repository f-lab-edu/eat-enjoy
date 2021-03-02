package com.restaurant.eatenjoy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.eatenjoy.dto.UserDto;
import com.restaurant.eatenjoy.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	@Autowired
	private final UserService userService;

	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public void createUsers(@Validated @RequestBody UserDto userDto) {
		userService.register(userDto);
	}
}
