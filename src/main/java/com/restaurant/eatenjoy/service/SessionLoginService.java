package com.restaurant.eatenjoy.service;

import java.util.function.Function;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.restaurant.eatenjoy.dto.LoginDto;
import com.restaurant.eatenjoy.dto.OwnerDto;
import com.restaurant.eatenjoy.dto.UserDto;
import com.restaurant.eatenjoy.exception.AuthorizationException;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.exception.UnauthorizedException;
import com.restaurant.eatenjoy.util.Role;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionLoginService implements LoginService {

	private static final String LOGIN_AUTH_ID = "LOGIN_AUTH_ID";

	private static final String AUTH_ROLE = "AUTH_ROLE";

	private final HttpSession httpSession;

	private final UserService userService;

	private final OwnerService ownerService;

	@Override
	public void loginUser(LoginDto loginDto) {
		login(loginDto, userService::findIdByLoginIdAndPassword, Role.USER);
	}

	@Override
	public void loginOwner(LoginDto loginDto) {
		login(loginDto, ownerService::findIdByLoginIdAndPassword, Role.OWNER);
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
	public void validateUserAuthority() {
		validateAuthority(Role.USER, loginAuthId -> {
			UserDto userDto = userService.findById(loginAuthId);
			if (userDto == null) {
				throw new AuthorizationException();
			}

			return userDto.isCertified();
		});
	}

	@Override
	public void validateOwnerAuthority() {
		validateAuthority(Role.OWNER, loginAuthId -> {
			OwnerDto ownerDto = ownerService.findById(loginAuthId);
			if (ownerDto == null) {
				throw new AuthorizationException();
			}

			return ownerDto.isCertified();
		});
	}

	private void login(LoginDto loginDto, Function<LoginDto, Long> validator, Role role) {
		if (httpSession.getAttribute(LOGIN_AUTH_ID) != null) {
			throw new DuplicateValueException("이미 로그인이 되어 있습니다.");
		}

		httpSession.setAttribute(LOGIN_AUTH_ID, validator.apply(loginDto));
		httpSession.setAttribute(AUTH_ROLE, role);
	}

	private void validateAuthority(Role role, Function<Long, Boolean> mailValidator) {
		if (role != getAuthRole()) {
			throw new AuthorizationException();
		}

		if (!mailValidator.apply(getLoginAuthId())) {
			throw new AuthorizationException("메일 인증이 되지 않았습니다.");
		}
	}

}
