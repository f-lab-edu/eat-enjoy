package com.restaurant.eatenjoy.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.eatenjoy.annotation.Authority;
import com.restaurant.eatenjoy.annotation.LoginAuthId;
import com.restaurant.eatenjoy.dto.LoginDto;
import com.restaurant.eatenjoy.dto.PasswordDto;
import com.restaurant.eatenjoy.dto.UpdatePasswordDto;
import com.restaurant.eatenjoy.dto.UpdateUserDto;
import com.restaurant.eatenjoy.dto.UserDto;
import com.restaurant.eatenjoy.dto.UserInfoDto;
import com.restaurant.eatenjoy.util.security.LoginService;
import com.restaurant.eatenjoy.service.UserService;
import com.restaurant.eatenjoy.util.security.Role;

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
		loginService.login(loginDto, userService);
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
	public void resendMail(@LoginAuthId Long userId) {
		userService.resendCertificationMail(userId);
	}

	@Authority(Role.USER)
	@DeleteMapping("/my-infos")
	public void delete(@LoginAuthId Long userId, @RequestBody @Valid PasswordDto passwordDto) {
		userService.delete(userId, passwordDto.getPassword());
		loginService.logout();
	}

	@Authority(Role.USER)
	@PatchMapping("/my-infos/password")
	public void changePassword(@LoginAuthId Long userId, @RequestBody @Valid UpdatePasswordDto passwordDto) {
		userService.updatePassword(userId, passwordDto);
	}

	@Authority(Role.USER)
	@GetMapping("/my-infos")
	public UserInfoDto userInfo(@LoginAuthId Long userId) {
		return userService.getUserInfo(userId);
	}

	@Authority(Role.USER)
	@PatchMapping("/my-infos")
	public void update(@LoginAuthId Long userId, @RequestBody @Valid UpdateUserDto userDto) {
		userService.update(userId, userDto);
	}

}
