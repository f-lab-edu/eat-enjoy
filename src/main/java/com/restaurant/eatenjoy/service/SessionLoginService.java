package com.restaurant.eatenjoy.service;

import java.util.function.Consumer;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.restaurant.eatenjoy.dto.LoginDto;
import com.restaurant.eatenjoy.dto.OwnerDto;
import com.restaurant.eatenjoy.dto.UserDto;
import com.restaurant.eatenjoy.exception.AuthorizationException;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.exception.UnauthorizedException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionLoginService implements LoginService {

	private static final String LOGIN_USER_ID = "LOGIN_USER_ID";

	private static final String LOGIN_OWNER_ID = "LOGIN_OWNER_ID";

	private final HttpSession httpSession;

	private final UserService userService;

	private final OwnerService ownerService;

	@Override
	public void loginUser(LoginDto loginDto) {
		login(LOGIN_USER_ID, loginDto, userService::validateLoginIdAndPassword);
	}

	@Override
	public void loginOwner(LoginDto loginDto) {
		login(LOGIN_OWNER_ID, loginDto, ownerService::validateLoginIdAndPassword);
	}

	private void login(String sessionKey, LoginDto loginDto, Consumer<LoginDto> validator) {
		if (httpSession.getAttribute(sessionKey) != null) {
			throw new DuplicateValueException("이미 로그인이 되어 있습니다.");
		}

		validator.accept(loginDto);
		httpSession.setAttribute(sessionKey, loginDto.getLoginId());
	}

	@Override
	public void logout() {
		httpSession.invalidate();
	}

	@Override
	public String getLoginUserId() {
		return getLoginId(LOGIN_USER_ID);
	}

	@Override
	public String getLoginOwnerId() {
		return getLoginId(LOGIN_OWNER_ID);
	}

	private String getLoginId(String sessionKey) {
		Object loginId = httpSession.getAttribute(sessionKey);
		if (loginId == null) {
			throw new UnauthorizedException();
		}

		return (String) loginId;
	}

	@Override
	public void validateUserAuthority() {
		UserDto userDto = userService.findByLoginId(getLoginUserId());
		if (userDto == null) {
			throw new AuthorizationException();
		}

		if (!userDto.isCertified()) {
			throw new AuthorizationException("메일 인증이 되지 않았습니다.");
		}
	}

	@Override
	public void validateOwnerAuthority() {
		OwnerDto ownerDto = ownerService.findByLoginId(getLoginOwnerId());
		if (ownerDto == null) {
			throw new AuthorizationException();
		}

		if (!ownerDto.isCertified()) {
			throw new AuthorizationException("메일 인증이 되지 않았습니다.");
		}
	}

}
