package com.restaurant.eatenjoy.util.security;

import com.restaurant.eatenjoy.dto.security.LoginDto;

public interface LoginService {

	void login(LoginDto loginDto, UserDetailsService userDetailsService);

	void logout();

	Long getLoginAuthId();

	Role getAuthRole();

	void validateAuthority(UserDetailsService userDetailsService);

}
