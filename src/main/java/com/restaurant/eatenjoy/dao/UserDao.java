package com.restaurant.eatenjoy.dao;

import org.apache.ibatis.annotations.Param;

import com.restaurant.eatenjoy.dto.UserDto;

public interface UserDao {

	void register(UserDto userDto);

	boolean existsByLoginId(String loginId);

	boolean existsByEmail(String email);

	boolean existsByLoginIdAndPassword(@Param("loginId") String loginId, @Param("password") String password);

	void updateEmailCertified(String email);

	UserDto findByLoginId(String loginId);

	void deleteByLoginId(String loginId);

}
