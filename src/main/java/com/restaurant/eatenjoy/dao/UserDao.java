package com.restaurant.eatenjoy.dao;

import org.apache.ibatis.annotations.Param;

import com.restaurant.eatenjoy.dto.LoginDto;
import com.restaurant.eatenjoy.dto.UserDto;

public interface UserDao {

	void register(UserDto userDto);

	boolean existsByLoginId(String loginId);

	boolean existsByEmail(String email);

	boolean existsByLoginIdAndPassword(LoginDto loginDto);

	boolean existsByEmailAndEmailToken(@Param("email") String email, @Param("emailToken") String emailToken);

	void updateEmailCertified(String email);

}
