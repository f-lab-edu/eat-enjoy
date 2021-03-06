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
import com.restaurant.eatenjoy.dto.security.LoginDto;
import com.restaurant.eatenjoy.dto.security.MailDto;
import com.restaurant.eatenjoy.dto.owner.OwnerDto;
import com.restaurant.eatenjoy.dto.owner.OwnerInfoDto;
import com.restaurant.eatenjoy.dto.security.PasswordDto;
import com.restaurant.eatenjoy.dto.security.UpdatePasswordDto;
import com.restaurant.eatenjoy.util.security.LoginService;
import com.restaurant.eatenjoy.service.OwnerService;
import com.restaurant.eatenjoy.util.security.Role;

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
		loginService.login(loginDto, ownerService);
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
	public void resendMail(@LoginAuthId Long ownerId) {
		ownerService.resendCertificationMail(ownerId);
	}

	@Authority(Role.OWNER)
	@DeleteMapping("/my-infos")
	public void delete(@LoginAuthId Long ownerId, @RequestBody @Valid PasswordDto passwordDto) {
		ownerService.delete(ownerId, passwordDto.getPassword());
		loginService.logout();
	}

	@Authority(Role.OWNER)
	@PatchMapping("/my-infos/password")
	public void changePassword(@LoginAuthId Long ownerId, @RequestBody @Valid UpdatePasswordDto passwordDto) {
		ownerService.updatePassword(ownerId, passwordDto);
	}

	@Authority(Role.OWNER)
	@GetMapping("/my-infos")
	public OwnerInfoDto ownerInfo(@LoginAuthId Long ownerId) {
		return ownerService.getOwnerInfo(ownerId);
	}

	@Authority(Role.OWNER)
	@PatchMapping("/my-infos/mail")
	public void changeMail(@LoginAuthId Long ownerId, @RequestBody @Valid MailDto mailDto) {
		ownerService.changeMail(ownerId, mailDto);
	}

}
