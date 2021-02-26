package com.restaurant.eatenjoy.service;

import org.springframework.stereotype.Service;

import com.restaurant.eatenjoy.dto.UserDto;

@Service
public interface UserService {
	void register(UserDto userDto);

	boolean isDuplicatedId(String loginId);

	boolean isDuplicatedEmail(String email);
}
