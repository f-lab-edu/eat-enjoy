package com.restaurant.eatenjoy.util.security;

import com.restaurant.eatenjoy.dto.LoginDto;

public interface UserDetailsService {

	Long findIdByLoginIdAndPassword(LoginDto loginDto);

	boolean isMailCertified(Long id);

	Role getRole();

}
