package com.restaurant.eatenjoy.service.impl;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.restaurant.eatenjoy.dto.LoginDto;
import com.restaurant.eatenjoy.exception.AlreadyLoginException;
import com.restaurant.eatenjoy.exception.NoUserFoundException;
import com.restaurant.eatenjoy.exception.UserSessionNotExistException;
import com.restaurant.eatenjoy.service.LoginService;
import com.restaurant.eatenjoy.service.UserService;
import com.restaurant.eatenjoy.util.HttpResponseStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionLoginService implements LoginService {

	private final String USERSESSION = "USERSSESSION";

	@Autowired
	private final HttpSession httpSession;

	@Autowired
	private final UserService userService;

	@Override
	public void login(LoginDto loginDto) {

		userService.isExistUserbyLoginIdAndPassword(loginDto);

		if (httpSession.getAttribute(USERSESSION) != null) {
			throw new AlreadyLoginException("이미 로그인 하였습니다");
		}

		// 로그인 성공
		httpSession.setAttribute(USERSESSION, loginDto);
	}

	@Override
	public void logout() {

		if (httpSession.getAttribute(USERSESSION) == null) {
			throw new UserSessionNotExistException("유저 세션이 존재하지 않습니다");
		}

		httpSession.removeAttribute(USERSESSION);
	}
}
