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
import com.restaurant.eatenjoy.annotation.LoginOwnerId;
import com.restaurant.eatenjoy.annotation.LoginUserId;
import com.restaurant.eatenjoy.dto.LoginDto;
import com.restaurant.eatenjoy.dto.OwnerDto;
import com.restaurant.eatenjoy.dto.PasswordDto;
import com.restaurant.eatenjoy.service.LoginService;
import com.restaurant.eatenjoy.service.OwnerService;
import com.restaurant.eatenjoy.util.Role;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class OwnerController {

	private final OwnerService ownerService;

	private final LoginService loginService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void register(@RequestBody @Valid OwnerDto ownerDto) {
		ownerService.register(ownerDto);
	}

	@PostMapping("/login")
	public void login(@RequestBody @Valid LoginDto loginDto) {
		loginService.loginOwner(loginDto);
	}

	@PostMapping("/logout")
	public void logout() {
		loginService.logout();
	}

	@GetMapping("/check-mail-token")
	public void checkMailToken(String email, String token) {
		ownerService.certifyEmailToken(email, token);
	}

	@GetMapping("/resend-mail")
	public void resendMail(@LoginUserId String loginId) {
		ownerService.resendCertificationMail(loginId);
	}

	@Authority(Role.OWNER)
	@DeleteMapping("/my-info")
	public void delete(@LoginOwnerId String loginId, @RequestBody @Valid PasswordDto passwordDto) {
		ownerService.delete(loginId, passwordDto.getPassword());
		loginService.logout();
	}

}
