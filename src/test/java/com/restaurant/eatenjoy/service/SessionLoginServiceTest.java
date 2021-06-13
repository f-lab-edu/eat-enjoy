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

import com.restaurant.eatenjoy.dto.security.LoginDto;
import com.restaurant.eatenjoy.exception.AuthorizationException;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.util.security.Role;
import com.restaurant.eatenjoy.util.security.UserDetailsService;

@ExtendWith(MockitoExtension.class)
class SessionLoginServiceTest {

	private static final String LOGIN_AUTH_ID = "LOGIN_AUTH_ID";

	private static final String AUTH_ROLE = "AUTH_ROLE";

	@Mock
	private UserDetailsService userDetailsService;

	@Spy
	private final HttpSession httpSession = new MockHttpSession();

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
		assertThatThrownBy(() -> loginService.login(loginDto, userDetailsService)).isInstanceOf(DuplicateValueException.class);
	}

	@Test
	@DisplayName("사용자 정보에 존재하는 로그인 아이디 & 비밀번호 요청이면 로그인에 성공한다.")
	void successToLogin() {
		given(userDetailsService.findIdByLoginIdAndPassword(loginDto)).willReturn(1L);

		loginService.login(loginDto, userDetailsService);

		assertThat(httpSession.getAttribute(LOGIN_AUTH_ID)).isEqualTo(1L);
		then(userDetailsService).should(times(1)).findIdByLoginIdAndPassword(loginDto);
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

		given(userDetailsService.getRole()).willReturn(Role.USER);
		given(userDetailsService.isMailCertified(loginService.getLoginAuthId())).willThrow(AuthorizationException.class);

		assertThatThrownBy(() -> loginService.validateAuthority(userDetailsService))
			.isInstanceOf(AuthorizationException.class)
			.hasMessage(null);

		then(userDetailsService).should(times(1)).getRole();
		then(userDetailsService).should(times(1)).isMailCertified(loginService.getLoginAuthId());
	}

	@Test
	@DisplayName("메일 인증을 하지 않으면 사용자 권한 검증에 실패한다.")
	void failToUserAuthorityIfUncertified() {
		httpSession.setAttribute(LOGIN_AUTH_ID, 1L);
		httpSession.setAttribute(AUTH_ROLE, Role.USER);

		given(userDetailsService.getRole()).willReturn(Role.USER);
		given(userDetailsService.isMailCertified(loginService.getLoginAuthId())).willReturn(false);

		assertThatThrownBy(() -> loginService.validateAuthority(userDetailsService))
			.isInstanceOf(AuthorizationException.class)
			.hasMessage("메일 인증이 되지 않았습니다.");

		then(userDetailsService).should(times(1)).getRole();
		then(userDetailsService).should(times(1)).isMailCertified(loginService.getLoginAuthId());
	}

	@Test
	@DisplayName("메일을 인증하면 사용자 권한 검증에 성공한다.")
	void successToUserAuthority() {
		httpSession.setAttribute(LOGIN_AUTH_ID, 1L);
		httpSession.setAttribute(AUTH_ROLE, Role.USER);

		given(userDetailsService.getRole()).willReturn(Role.USER);
		given(userDetailsService.isMailCertified(loginService.getLoginAuthId())).willReturn(true);

		loginService.validateAuthority(userDetailsService);

		then(userDetailsService).should(times(1)).getRole();
		then(userDetailsService).should(times(1)).isMailCertified(loginService.getLoginAuthId());
	}

}