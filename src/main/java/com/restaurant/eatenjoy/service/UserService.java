package com.restaurant.eatenjoy.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.eatenjoy.dao.UserDao;
import com.restaurant.eatenjoy.dto.LoginDto;
import com.restaurant.eatenjoy.dto.UserDto;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.exception.UserNotFoundException;
import com.restaurant.eatenjoy.util.encrypt.Encryptable;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserDao userDao;

	private final Encryptable encryptable;

	@Transactional
	public void register(UserDto userDto) {
		validateLoginIdAndEmail(userDto);
		userDao.register(UserDto.builder()
			.loginId(userDto.getLoginId())
			.password(encryptable.encrypt(userDto.getPassword()))
			.email(userDto.getEmail())
			.regionCd(userDto.getRegionCd())
			.build());
	}

	private void validateLoginIdAndEmail(UserDto userDto) {
		if (userDao.existsByLoginId(userDto.getLoginId())) {
			throw new DuplicateValueException("로그인 아이디가 이미 존재합니다.");
		}

		if (userDao.existsByEmail(userDto.getEmail())) {
			throw new DuplicateValueException("이메일 주소가 이미 존재합니다.");
		}
	}

	public void validateLoginIdAndPassword(LoginDto loginDto) {
		loginDto = LoginDto.builder()
			.loginId(loginDto.getLoginId())
			.password(encryptable.encrypt(loginDto.getPassword()))
			.build();

		if (!userDao.existsByLoginIdAndPassword(loginDto)) {
			throw new UserNotFoundException("아이디 또는 비밀번호가 일치하지 않습니다.");
		}
	}

}
