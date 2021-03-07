package com.restaurant.eatenjoy.service;

import com.restaurant.eatenjoy.dto.LoginDto;
import com.restaurant.eatenjoy.dto.UserDto;

public interface UserService {

	void register(UserDto userDto);

	boolean isDuplicatedId(String loginId);

	boolean isDuplicatedEmail(String email);

	void isExistUserbyLoginIdAndPassword(LoginDto loginDto);
}
