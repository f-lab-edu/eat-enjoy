package com.restaurant.eatenjoy.service;

import static org.assertj.core.api.Assertions.*;

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
import com.restaurant.eatenjoy.exception.DuplicateValueException;

@ExtendWith(MockitoExtension.class)
class SessionLoginServiceTest {

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
		httpSession.setAttribute("loginId", loginDto.getLoginId());
		assertThatThrownBy(() -> loginService.login(loginDto)).isInstanceOf(DuplicateValueException.class);
	}

	@Test
	@DisplayName("사용자 정보에 존재하는 로그인 아이디 & 비밀번호 요청이면 로그인에 성공한다.")
	void successToLogin() {
		loginService.login(loginDto);
		assertThat(httpSession.getAttribute("loginId")).isEqualTo(loginDto.getLoginId());
	}

	@Test
	@DisplayName("로그아웃에 성공한다.")
	void successToLogout() {
		loginService.logout();
	}

}