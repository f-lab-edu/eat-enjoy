package com.restaurant.eatenjoy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.eatenjoy.dto.UserDto;
import com.restaurant.eatenjoy.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/create")
	public ResponseEntity<UserDto> createUsers(@Validated @RequestBody UserDto userDto) {
		userService.register(userDto);

		return ResponseEntity.ok().body(userDto);
	}
}
