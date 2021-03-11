package com.restaurant.eatenjoy.service;

import com.restaurant.eatenjoy.dto.LoginDto;

public interface LoginService {

	void login(LoginDto loginDto);

	void logout();

	String getLoginId();

	void validateUserAuthority();

}
