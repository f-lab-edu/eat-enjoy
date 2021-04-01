package com.restaurant.eatenjoy.service;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.restaurant.eatenjoy.dto.LoginDto;
import com.restaurant.eatenjoy.exception.AuthorizationException;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.exception.UnauthorizedException;
import com.restaurant.eatenjoy.exception.UserNotFoundException;
import com.restaurant.eatenjoy.util.security.LoginService;
import com.restaurant.eatenjoy.util.security.Role;
import com.restaurant.eatenjoy.util.security.UserDetailsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionLoginService implements LoginService {

	private static final String LOGIN_AUTH_ID = "LOGIN_AUTH_ID";

	private static final String AUTH_ROLE = "AUTH_ROLE";

	private final HttpSession httpSession;

	@Override
	public void login(LoginDto loginDto, UserDetailsService userDetailsService) {
		if (httpSession.getAttribute(LOGIN_AUTH_ID) != null) {
			throw new DuplicateValueException("이미 로그인이 되어 있습니다.");
		}

		Long id = userDetailsService.findIdByLoginIdAndPassword(loginDto);
		if (id == null) {
			throw new UserNotFoundException("아이디 또는 비밀번호가 일치하지 않습니다.");
		}

		httpSession.setAttribute(LOGIN_AUTH_ID, id);
		httpSession.setAttribute(AUTH_ROLE, userDetailsService.getRole());
	}

	@Override
	public void logout() {
		httpSession.invalidate();
	}

	@Override
	public Long getLoginAuthId() {
		Object loginId = httpSession.getAttribute(LOGIN_AUTH_ID);
		if (loginId == null) {
			throw new UnauthorizedException();
		}

		return (Long) loginId;
	}

	@Override
	public Role getAuthRole() {
		Object role = httpSession.getAttribute(AUTH_ROLE);
		if (role == null) {
			throw new UnauthorizedException();
		}

		return (Role) role;
	}

	@Override
	public void validateAuthority(UserDetailsService userDetailsService) {
		if (userDetailsService.getRole() != getAuthRole()) {
			throw new AuthorizationException();
		}

		if (!userDetailsService.isMailCertified(getLoginAuthId())) {
			throw new AuthorizationException("메일 인증이 되지 않았습니다.");
		}
	}

}
