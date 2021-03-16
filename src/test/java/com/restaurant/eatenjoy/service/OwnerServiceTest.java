package com.restaurant.eatenjoy.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.restaurant.eatenjoy.dao.MailTokenDao;
import com.restaurant.eatenjoy.dao.OwnerDao;
import com.restaurant.eatenjoy.dto.OwnerDto;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.util.Role;
import com.restaurant.eatenjoy.util.encrypt.Encryptable;
import com.restaurant.eatenjoy.util.mail.MailService;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {

	public static final String TEST_MAIL = "test@test.com";

	@Mock
	private OwnerDao ownerDao;

	@Mock
	private Encryptable encryptable;

	@Mock
	private MailService mailService;

	@Mock
	private MailTokenDao mailTokenDao;

	@InjectMocks
	private OwnerService ownerService;

	private OwnerDto ownerDto;

	@BeforeEach
	void setUp() {
		ownerDto = OwnerDto.builder()
			.loginId("test")
			.password("1234")
			.email(TEST_MAIL)
			.build();
	}

	@Test
	@DisplayName("중복된 로그인 아이디는 회원가입을 할 수 없다.")
	void failToRegisterLoginIdDuplicated() {
		given(ownerDao.existsByLoginId("test")).willReturn(true);
		assertThatThrownBy(() -> ownerService.register(ownerDto)).isInstanceOf(DuplicateValueException.class);
		then(ownerDao).should(times(1)).existsByLoginId("test");
	}

	@Test
	@DisplayName("중복된 이메일은 회원가입을 할 수 없다.")
	void failToRegisterEmailDuplicated() {
		given(ownerDao.existsByEmail(TEST_MAIL)).willReturn(true);
		assertThatThrownBy(() -> ownerService.register(ownerDto)).isInstanceOf(DuplicateValueException.class);
		then(ownerDao).should(times(1)).existsByEmail(TEST_MAIL);
	}

	@Test
	@DisplayName("로그인 아이디와 이메일이 중복되지 않으면 회원가입을 성공하고 인증메일을 전송한다.")
	void successToRegisterAndSendMail() {
		given(ownerDao.existsByLoginId("test")).willReturn(false);
		given(ownerDao.existsByEmail(TEST_MAIL)).willReturn(false);
		given(encryptable.encrypt("1234")).willReturn("!@#$%^&*()");

		ownerService.register(ownerDto);

		then(ownerDao).should(times(1)).existsByLoginId("test");
		then(ownerDao).should(times(1)).existsByEmail(TEST_MAIL);
		then(encryptable).should(times(1)).encrypt("1234");
		then(mailService).should(times(1)).send(any());
		then(mailTokenDao).should(times(1)).create(eq(Role.OWNER), eq(TEST_MAIL), any(), eq(Duration.ofSeconds(86400)));
	}

}