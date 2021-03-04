package com.restaurant.eatenjoy.dao;

import org.apache.ibatis.annotations.Mapper;

import com.restaurant.eatenjoy.dto.UserDto;

@Mapper
public interface UserDao {

	void insertUser(UserDto userDto);

	boolean readUserLoginId(String loginId);

	boolean readUserEmail(String email);

	boolean readUserbyLoginIdAndPassword(String loginId, String password);
}
