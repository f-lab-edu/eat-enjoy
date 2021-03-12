package com.restaurant.eatenjoy.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.eatenjoy.annotation.Authority;
import com.restaurant.eatenjoy.annotation.CurrentLoginId;
import com.restaurant.eatenjoy.dto.LoginDto;
import com.restaurant.eatenjoy.dto.PasswordDto;
import com.restaurant.eatenjoy.dto.UserDto;
import com.restaurant.eatenjoy.service.LoginService;
import com.restaurant.eatenjoy.service.UserService;
import com.restaurant.eatenjoy.util.Role;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	private final LoginService loginService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void register(@RequestBody @Valid UserDto userDto) {
		userService.register(userDto);
	}

	@PostMapping("/login")
	public void login(@RequestBody @Valid LoginDto loginDto) {
		loginService.loginUser(loginDto);
	}

	@PostMapping("/logout")
	public void logout() {
		loginService.logout();
	}

	@GetMapping("/check-mail-token")
	public void checkMailToken(String email, String token) {
		userService.certifyEmailToken(email, token);
	}

	@GetMapping("/resend-mail")
	public void resendMail(@CurrentLoginId String loginId) {
		userService.resendCertificationMail(loginId);
	}

	@Authority(Role.USER)
	@DeleteMapping("/my-info")
	public void delete(@CurrentLoginId String loginId, @RequestBody @Valid PasswordDto passwordDto) {
		userService.delete(loginId, passwordDto.getPassword());
		loginService.logout();
	}

}
