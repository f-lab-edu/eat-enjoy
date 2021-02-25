package com.restaurant.eatenjoy.dao;

import com.restaurant.eatenjoy.dto.UserDto;

public interface UserDao {
	void insertUser(UserDto userDto);

	boolean readUserLoginId(String loginId);

	boolean readUserEmail(String email);
}
