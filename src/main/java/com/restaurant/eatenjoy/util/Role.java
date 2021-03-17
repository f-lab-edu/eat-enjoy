package com.restaurant.eatenjoy.util;

import java.util.function.Consumer;

import com.restaurant.eatenjoy.service.LoginService;

public enum Role {

	USER(LoginService::validateUserAuthority),
	OWNER(LoginService::validateOwnerAuthority);

	private final Consumer<LoginService> validator;

	Role(Consumer<LoginService> validator) {
		this.validator = validator;
	}

	public void validate(LoginService loginService) {
		validator.accept(loginService);
	}

}
