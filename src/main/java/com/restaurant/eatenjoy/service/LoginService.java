package com.restaurant.eatenjoy.service;

import com.restaurant.eatenjoy.dto.LoginDto;
import com.restaurant.eatenjoy.util.Role;

public interface LoginService {

	void loginUser(LoginDto loginDto);

	void loginOwner(LoginDto loginDto);

	void logout();

	Long getLoginAuthId();

	Role getAuthRole();

	void validateUserAuthority();

	void validateOwnerAuthority();

}
