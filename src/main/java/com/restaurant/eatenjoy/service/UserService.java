package com.restaurant.eatenjoy.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.eatenjoy.dao.UserDao;
import com.restaurant.eatenjoy.dto.UserDto;
import com.restaurant.eatenjoy.exception.DuplicateValueException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserDao userDao;

	@Transactional
	public void register(UserDto userDto) {
		validateLoginIdAndEmail(userDto);
		userDto.encryptPassword();
		userDao.register(userDto);
	}

	private void validateLoginIdAndEmail(UserDto userDto) {
		if (userDao.existsByLoginId(userDto.getLoginId())) {
			throw new DuplicateValueException("로그인 아이디가 이미 존재합니다.");
		}

		if (userDao.existsByEmail(userDto.getEmail())) {
			throw new DuplicateValueException("이메일 주소가 이미 존재합니다.");
		}
	}

}
