package com.restaurant.eatenjoy.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.restaurant.eatenjoy.dao.UserDao;
import com.restaurant.eatenjoy.dto.LoginDto;
import com.restaurant.eatenjoy.dto.UserDto;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.exception.UserNotFoundException;
import com.restaurant.eatenjoy.util.encrypt.Encryptable;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserDao userDao;

	@Mock
	private Encryptable encryptable;

	@InjectMocks
	private UserService userService;

	private UserDto userDto;

	@BeforeEach
	void setUp() {
		userDto = UserDto.builder()
			.loginId("test")
			.password("1234")
			.email("test@test.com")
			.regionCd("001")
			.build();
	}

	@Test
	@DisplayName("중복된 로그인 아이디는 회원가입을 할 수 없다.")
	void failToRegisterLoginIdDuplicated() {
		given(userDao.existsByLoginId("test")).willReturn(true);
		assertThatThrownBy(() -> userService.register(userDto)).isInstanceOf(DuplicateValueException.class);
		then(userDao).should(times(1)).existsByLoginId("test");
	}

	@Test
	@DisplayName("중복된 이메일은 회원가입을 할 수 없다.")
	void failToRegisterEmailDuplicated() {
		given(userDao.existsByEmail("test@test.com")).willReturn(true);
		assertThatThrownBy(() -> userService.register(userDto)).isInstanceOf(DuplicateValueException.class);
		then(userDao).should(times(1)).existsByEmail("test@test.com");
	}

	@Test
	@DisplayName("로그인 아이디와 이메일이 중복되지 않으면 회원가입을 성공한다.")
	void successToRegister() {
		given(userDao.existsByLoginId("test")).willReturn(false);
		given(userDao.existsByEmail("test@test.com")).willReturn(false);
		given(encryptable.encrypt("1234")).willReturn("!@#$%^&*()");

		userService.register(userDto);

		then(userDao).should(times(1)).existsByLoginId("test");
		then(userDao).should(times(1)).existsByEmail("test@test.com");
		then(encryptable).should(times(1)).encrypt("1234");
	}

	@Test
	@DisplayName("로그인 정보로 사용자를 찾지 못하면 UserNotFoundException 예외가 발생한다.")
	void failToLoginUserNotFound() {
		given(userDao.existsByLoginIdAndPassword(any())).willReturn(false);

		assertThatThrownBy(() -> userService.validateLoginIdAndPassword(LoginDto.builder()
			.loginId("test")
			.password("1111")
			.build())).isInstanceOf(UserNotFoundException.class);

		then(userDao).should(times(1)).existsByLoginIdAndPassword(any());
	}

	@Test
	@DisplayName("로그인 정보로 사용자가 존재하면 정상이다.")
	void normalToLoginUserFound() {
		given(userDao.existsByLoginIdAndPassword(any())).willReturn(true);

		userService.validateLoginIdAndPassword(LoginDto.builder()
			.loginId("test")
			.password("1234")
			.build());

		then(userDao).should(times(1)).existsByLoginIdAndPassword(any());
	}

}