package com.restaurant.eatenjoy.service;

import com.restaurant.eatenjoy.dto.LoginDto;

public interface LoginService {

	void loginUser(LoginDto loginDto);

	void loginOwner(LoginDto loginDto);

	void logout();

	String getLoginUserId();

	String getLoginOwnerId();

	void validateUserAuthority();

	void validateOwnerAuthority();

}
