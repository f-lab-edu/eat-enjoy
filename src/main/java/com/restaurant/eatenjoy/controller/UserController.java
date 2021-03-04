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
		boolean isUser = userService.isExistUserbyLoginIdAndPassword(loginDto);
		boolean isLoginUser = loginService.isLoginUser();

		// 아이디 혹은 비밀번호가 존재하지 않은 경우
		if (!isUser) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		// 이미 로그인을 한 경우
		if (isLoginUser) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		
		// 로그인 성공
		loginService.login(loginDto);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PostMapping("/logout")
	public ResponseEntity<HttpStatus> logoutUser() {

		// 로그인한 사용자가 존재하지 않기 때문에 BAD_REQUEST return
		if (!loginService.isLoginUser()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		// 로그아웃 성공
		loginService.logout();
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
