package com.restaurant.eatenjoy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.restaurant.eatenjoy.dao.UserDao;
import com.restaurant.eatenjoy.dto.UserDto;
import com.restaurant.eatenjoy.exception.DuplicatedException;
import com.restaurant.eatenjoy.service.UserService;
import com.restaurant.eatenjoy.util.EncryptUtil;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private EncryptUtil encryptUtil;

	@Override
	public void register(UserDto userDto) {

		if (isDuplicatedId(userDto.getLoginId())) {
			throw new DuplicatedException("중복된 아이디 입니다");
		}

		if (isDuplicatedEmail(userDto.getEmail())) {
			throw new DuplicatedException("중복된 이메일 입니다");
		}

		String password = encryptUtil.encrypt(userDto.getPassword());
		userDto.setPassword(password);

		userDao.insertUser(userDto);
	}

	@Override
	public boolean isDuplicatedId(String loginId) {
		boolean loginIdCheckResult = userDao.readUserLoginId(loginId);

		if (loginIdCheckResult) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isDuplicatedEmail(String email) {
		boolean emailCheckResult = userDao.readUserEmail(email);

		if (emailCheckResult) {
			return true;
		} else {
			return false;
		}
	}
}
