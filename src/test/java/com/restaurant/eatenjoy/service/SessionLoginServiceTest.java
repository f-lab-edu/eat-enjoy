package com.restaurant.eatenjoy.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;

import com.restaurant.eatenjoy.dto.LoginDto;
import com.restaurant.eatenjoy.dto.UserDto;
import com.restaurant.eatenjoy.exception.AuthorizationException;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.util.Role;

@ExtendWith(MockitoExtension.class)
class SessionLoginServiceTest {

	private static final String LOGIN_AUTH_ID = "LOGIN_AUTH_ID";

	private static final String AUTH_ROLE = "AUTH_ROLE";

	@Mock
	private UserService userService;

	@Spy
	private HttpSession httpSession = new MockHttpSession();

	@InjectMocks
	private SessionLoginService loginService;

	private LoginDto loginDto;

	@BeforeEach
	void setUp() {
		loginDto = LoginDto.builder()
			.loginId("test")
			.password("1234")
			.build();
	}

	@Test
	@DisplayName("동일한 로그인 아이디 세션이 존재하면 로그인은 실패한다.")
	void failToLoginSessionDuplicated() {
		httpSession.setAttribute(LOGIN_AUTH_ID, loginDto.getLoginId());
		assertThatThrownBy(() -> loginService.loginUser(loginDto)).isInstanceOf(DuplicateValueException.class);
	}

	@Test
	@DisplayName("사용자 정보에 존재하는 로그인 아이디 & 비밀번호 요청이면 로그인에 성공한다.")
	void successToLogin() {
		given(userService.findIdByLoginIdAndPassword(loginDto)).willReturn(1L);

		loginService.loginUser(loginDto);

		assertThat(httpSession.getAttribute(LOGIN_AUTH_ID)).isEqualTo(1L);
		then(userService).should(times(1)).findIdByLoginIdAndPassword(loginDto);
	}

	@Test
	@DisplayName("로그아웃에 성공한다.")
	void successToLogout() {
		loginService.logout();
	}

	@Test
	@DisplayName("사용자를 찾을 수 없으면 사용자 권한 검증에 실패한다.")
	void failToUserAuthorityIfUserNotFound() {
		httpSession.setAttribute(LOGIN_AUTH_ID, 1L);
		httpSession.setAttribute(AUTH_ROLE, Role.USER);

		given(userService.findById(loginService.getLoginAuthId())).willReturn(null);

		assertThatThrownBy(() -> loginService.validateUserAuthority())
			.isInstanceOf(AuthorizationException.class)
			.hasMessage(null);

		then(userService).should(times(1)).findById(loginService.getLoginAuthId());
	}

	@Test
	@DisplayName("메일 인증을 하지 않으면 사용자 권한 검증에 실패한다.")
	void failToUserAuthorityIfUncertified() {
		httpSession.setAttribute(LOGIN_AUTH_ID, 1L);
		httpSession.setAttribute(AUTH_ROLE, Role.USER);

		UserDto userDto = UserDto.builder()
			.loginId(loginDto.getLoginId())
			.certified(false)
			.build();

		given(userService.findById(loginService.getLoginAuthId())).willReturn(userDto);

		assertThatThrownBy(() -> loginService.validateUserAuthority())
			.isInstanceOf(AuthorizationException.class)
			.hasMessage("메일 인증이 되지 않았습니다.");

		then(userService).should(times(1)).findById(loginService.getLoginAuthId());
	}

	@Test
	@DisplayName("메일을 인증하면 사용자 권한 검증에 성공한다.")
	void successToUserAuthority() {
		httpSession.setAttribute(LOGIN_AUTH_ID, 1L);
		httpSession.setAttribute(AUTH_ROLE, Role.USER);

		UserDto userDto = UserDto.builder()
			.id(1L)
			.certified(true)
			.build();

		given(userService.findById(loginService.getLoginAuthId())).willReturn(userDto);

		loginService.validateUserAuthority();

		then(userService).should(times(1)).findById(loginService.getLoginAuthId());
	}

}