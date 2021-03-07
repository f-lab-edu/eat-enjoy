package com.restaurant.eatenjoy.dao;

import com.restaurant.eatenjoy.dto.LoginDto;
import com.restaurant.eatenjoy.dto.UserDto;

public interface UserDao {

	void register(UserDto userDto);

	boolean existsByLoginId(String loginId);

	boolean existsByEmail(String email);

	boolean existsByLoginIdAndPassword(LoginDto loginDto);

	void updateEmailCertified(String email);

}
