package com.restaurant.eatenjoy.service;

import java.util.function.Consumer;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.restaurant.eatenjoy.dto.LoginDto;
import com.restaurant.eatenjoy.dto.UserDto;
import com.restaurant.eatenjoy.exception.AuthorizationException;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.exception.UnauthorizedException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionLoginService implements LoginService {

	private static final String LOGIN_ID = "loginId";

	private final HttpSession httpSession;

	private final UserService userService;

	@Override
	public void loginUser(LoginDto loginDto) {
		login(loginDto, userService::validateLoginIdAndPassword);
	}

	private void login(LoginDto loginDto, Consumer<LoginDto> validator) {
		if (httpSession.getAttribute(LOGIN_ID) != null) {
			throw new DuplicateValueException("이미 로그인이 되어 있습니다.");
		}

		validator.accept(loginDto);
		httpSession.setAttribute(LOGIN_ID, loginDto.getLoginId());
	}

	@Override
	public void logout() {
		httpSession.invalidate();
	}

	@Override
	public String getLoginId() {
		Object loginId = httpSession.getAttribute(LOGIN_ID);
		if (loginId == null) {
			throw new UnauthorizedException();
		}

		return (String) loginId;
	}

	@Override
	public void validateUserAuthority() {
		UserDto userDto = userService.findByLoginId(getLoginId());
		if (userDto == null) {
			throw new AuthorizationException();
		}

		if (!userDto.isCertified()) {
			throw new AuthorizationException("메일 인증이 되지 않았습니다.");
		}
	}

}
