package com.restaurant.eatenjoy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.eatenjoy.dto.LoginDto;
import com.restaurant.eatenjoy.dto.UserDto;
import com.restaurant.eatenjoy.service.LoginService;
import com.restaurant.eatenjoy.service.UserService;
import com.restaurant.eatenjoy.util.HttpResponseStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	@Autowired
	private final UserService userService;

	@Autowired
	private final LoginService loginService;

	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public void createUsers(@Validated @RequestBody UserDto userDto) {
		userService.register(userDto);
	}

	@PostMapping("/login")
	public ResponseEntity<HttpStatus> loginUser(@Validated @RequestBody LoginDto loginDto) {

		loginService.login(loginDto);
		return HttpResponseStatus.OK;
	}

	@PostMapping("/logout")
	public ResponseEntity<HttpStatus> logoutUser() {

		loginService.logout();
		return HttpResponseStatus.OK;
	}
}
