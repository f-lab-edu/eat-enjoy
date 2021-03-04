package com.restaurant.eatenjoy.service.impl;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.restaurant.eatenjoy.dto.LoginDto;
import com.restaurant.eatenjoy.service.LoginService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionLoginServiceImpl implements LoginService {

	private final String USERSESSION = "USERSSESSION";

	@Autowired
	private final HttpSession httpSession;

	@Override
	public boolean login(LoginDto loginDto) {
		httpSession.setAttribute(USERSESSION, loginDto);

		return true;
	}

	@Override
	public boolean isLoginUser() {
		
		// 로그인한 아이디의 세션이 존재함
		if (httpSession.getAttribute(USERSESSION) != null) {
			return true;
		}

		// 로그인한 아이디의 세션이 존재하지 않음
		return false;
	}

	@Override
	public void logout() {
		httpSession.removeAttribute(USERSESSION);
	}
}
