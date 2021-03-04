package com.restaurant.eatenjoy.service;

import com.restaurant.eatenjoy.dto.LoginDto;

public interface LoginService {

	boolean login(LoginDto loginDto);

	boolean isLoginUser();

	void logout();
}
