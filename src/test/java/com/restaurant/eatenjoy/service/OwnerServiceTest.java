package com.restaurant.eatenjoy.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

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
import com.restaurant.eatenjoy.dto.security.LoginDto;
import com.restaurant.eatenjoy.dto.security.MailDto;
import com.restaurant.eatenjoy.dto.owner.OwnerDto;
import com.restaurant.eatenjoy.dto.security.UpdatePasswordDto;
import com.restaurant.eatenjoy.exception.AlreadyCertifiedException;
import com.restaurant.eatenjoy.exception.ConflictPasswordException;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.exception.MailTokenNotFoundException;
import com.restaurant.eatenjoy.exception.NoMatchedPasswordException;
import com.restaurant.eatenjoy.exception.UserNotFoundException;
import com.restaurant.eatenjoy.util.security.Role;
import com.restaurant.eatenjoy.util.security.encrypt.Encryptable;
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
			.id(1L)
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

	@Test
	@DisplayName("로그인 정보로 사용자가 존재하면 정상이다.")
	void normalToLoginUserFound() {
		given(ownerDao.findIdByLoginIdAndPassword(eq("test"), any())).willReturn(1L);

		ownerService.findIdByLoginIdAndPassword(LoginDto.builder()
			.loginId("test")
			.password("1234")
			.build());

		then(ownerDao).should(times(1)).findIdByLoginIdAndPassword(eq("test"), any());
	}

	@Test
	@DisplayName("메일로 사용자를 찾을 수 없으면 메일 인증에 실패한다.")
	void failToCertifyEmailTokenUserNotFound() {
		given(ownerDao.existsByEmail(TEST_MAIL)).willReturn(false);
		assertThatThrownBy(() -> ownerService.certifyEmailToken(TEST_MAIL, "1234"))
			.isInstanceOf(UserNotFoundException.class);
		then(ownerDao).should(times(1)).existsByEmail(TEST_MAIL);
	}

	@Test
	@DisplayName("인증 토큰이 일치하지 않으면 메일 인증에 실패한다.")
	void failToCertifyEmailTokenNotMatch() {
		given(ownerDao.existsByEmail(TEST_MAIL)).willReturn(true);
		given(mailTokenDao.findByRoleAndMail(eq(Role.OWNER), eq(TEST_MAIL))).willReturn("1111");

		assertThatThrownBy(() -> ownerService.certifyEmailToken(TEST_MAIL, "1234"))
			.isInstanceOf(MailTokenNotFoundException.class);

		then(ownerDao).should(times(1)).existsByEmail(TEST_MAIL);
		then(mailTokenDao).should(times(1)).findByRoleAndMail(eq(Role.OWNER), eq(TEST_MAIL));
	}

	@Test
	@DisplayName("메일 인증에 성공한다.")
	void successToCertifyEmailToken() {
		given(ownerDao.existsByEmail(TEST_MAIL)).willReturn(true);
		given(mailTokenDao.findByRoleAndMail(eq(Role.OWNER), eq(TEST_MAIL))).willReturn("1234");

		ownerService.certifyEmailToken(TEST_MAIL, "1234");

		then(ownerDao).should(times(1)).existsByEmail(TEST_MAIL);
		then(mailTokenDao).should(times(1)).findByRoleAndMail(eq(Role.OWNER), eq(TEST_MAIL));
		then(ownerDao).should(times(1)).updateEmailCertified(TEST_MAIL);
	}

	@Test
	@DisplayName("인증을 완료하면 인증 메일을 재전송할 수 없다.")
	void failToResendMailCertified() {
		ownerDto = OwnerDto.builder()
			.id(1L)
			.certified(true)
			.build();

		given(ownerDao.findById(1L)).willReturn(ownerDto);

		assertThatThrownBy(() -> ownerService.resendCertificationMail(1L))
			.isInstanceOf(AlreadyCertifiedException.class);

		then(ownerDao).should(times(1)).findById(1L);
	}

	@Test
	@DisplayName("인증 메일을 재전송한다.")
	void resendCertificationMail() {
		ownerDto = OwnerDto.builder()
			.id(1L)
			.email(TEST_MAIL)
			.certified(false)
			.build();

		given(ownerDao.findById(1L)).willReturn(ownerDto);

		ownerService.resendCertificationMail(1L);

		then(mailService).should(times(1)).send(any());
		then(mailTokenDao).should(times(1)).create(eq(Role.OWNER), eq(TEST_MAIL), any(), eq(Duration.ofSeconds(86400)));
	}

	@Test
	@DisplayName("비밀번호가 일치하지 않으면 회원탈퇴에 실패한다.")
	void failToMemberWithdrawalIfPasswordNotMatch() {
		given(ownerDao.existsByIdAndPassword(eq(1L), any())).willReturn(false);
		assertThatThrownBy(() -> ownerService.delete(1L, "1234"))
			.isInstanceOf(NoMatchedPasswordException.class);
		then(ownerDao).should(times(1)).existsByIdAndPassword(eq(1L), any());
	}

	@Test
	@DisplayName("비밀번호가 일치하면 회원탈퇴에 성공한다.")
	void successToMemberWithdrawal() {
		given(ownerDao.existsByIdAndPassword(eq(1L), any())).willReturn(true);
		ownerService.delete(1L, "1234");
		then(ownerDao).should(times(1)).existsByIdAndPassword(eq(1L), any());
	}

	@Test
	@DisplayName("기존 비밀번호로 유효하지 않으면 비밀번호 업데이트에 실패한다.")
	void failToUpdatePasswordIfOldPasswordInvalid() {
		given(ownerDao.existsByIdAndPassword(eq(1L), any())).willReturn(false);

		assertThatThrownBy(() -> ownerService.updatePassword(1L, UpdatePasswordDto.builder()
			.oldPassword("1234")
			.newPassword("5678")
			.build()))
			.isInstanceOf(NoMatchedPasswordException.class);

		then(ownerDao).should(times(1)).existsByIdAndPassword(eq(1L), any());
	}

	@Test
	@DisplayName("신규 비밀번호가 기존 비밀번호와 일치할 경우 비밀번호 업데이트에 실패한다.")
	void failToUpdatePasswordIfNewPasswordEqualsOldPassword() {
		given(ownerDao.existsByIdAndPassword(eq(1L), any())).willReturn(true);

		assertThatThrownBy(() -> ownerService.updatePassword(1L, UpdatePasswordDto.builder()
			.oldPassword("1234")
			.newPassword("1234")
			.build()))
			.isInstanceOf(ConflictPasswordException.class);

		then(ownerDao).should(times(1)).existsByIdAndPassword(eq(1L), any());
	}

	@Test
	@DisplayName("비밀번호 업데이트에 성공한다.")
	void successToUpdatePassword() {
		given(ownerDao.existsByIdAndPassword(eq(1L), any())).willReturn(true);

		ownerService.updatePassword(1L, UpdatePasswordDto.builder()
			.oldPassword("1234")
			.newPassword("5678")
			.build());

		then(ownerDao).should(times(1)).existsByIdAndPassword(eq(1L), any());
		then(ownerDao).should(times(1)).updatePassword(eq(1L), any());
	}

	@Test
	@DisplayName("메일을 변경하면 인증 메일을 전송한다.")
	void sendCertificationMailIfMailChange() {
		String changeMail = "change@test.com";
		given(ownerDao.findById(1L)).willReturn(OwnerDto.builder()
			.loginId("test")
			.email(changeMail)
			.certified(false)
			.build());

		ownerService.changeMail(1L, MailDto.builder()
			.email(changeMail)
			.build());

		then(ownerDao).should(times(1)).findById(1L);
		then(mailService).should(times(1)).send(any());
		then(mailTokenDao).should(times(1)).create(eq(Role.OWNER), eq(changeMail), any(), eq(Duration.ofSeconds(86400)));
	}

	@Test
	@DisplayName("메일이 변경되지 않았으면 메일을 전송하지 않는다.")
	void notSendCertificationMailIfMailNotChange() {
		given(ownerDao.findById(1L)).willReturn(OwnerDto.builder()
			.loginId("test")
			.email(TEST_MAIL)
			.certified(true)
			.build());

		ownerService.changeMail(1L, MailDto.builder()
			.email(TEST_MAIL)
			.build());

		then(ownerDao).should(times(1)).findById(1L);
		then(mailService).should(times(0)).send(any());
		then(mailTokenDao).should(times(0)).create(eq(Role.OWNER), eq(TEST_MAIL), any(), eq(Duration.ofSeconds(86400)));
	}

}