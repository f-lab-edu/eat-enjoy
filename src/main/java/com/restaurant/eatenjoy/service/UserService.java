package com.restaurant.eatenjoy.service;

import java.time.Duration;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.eatenjoy.dao.MailTokenDao;
import com.restaurant.eatenjoy.dao.UserDao;
import com.restaurant.eatenjoy.dto.LoginDto;
import com.restaurant.eatenjoy.dto.UserDto;
import com.restaurant.eatenjoy.exception.AlreadyCertifiedException;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.exception.MailTokenNotFoundException;
import com.restaurant.eatenjoy.exception.UserNotFoundException;
import com.restaurant.eatenjoy.util.encrypt.Encryptable;
import com.restaurant.eatenjoy.util.mail.MailMessage;
import com.restaurant.eatenjoy.util.mail.MailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private static final Duration MAIL_TOKEN_TIMEOUT_SECOND = Duration.ofSeconds(86400);

	private final UserDao userDao;

	private final Encryptable encryptable;

	private final MailService mailService;

	private final MailTokenDao mailTokenDao;

	@Transactional
	public void register(UserDto userDto) {
		validateLoginIdAndEmail(userDto);

		userDto = UserDto.builder()
			.loginId(userDto.getLoginId())
			.password(encryptable.encrypt(userDto.getPassword()))
			.email(userDto.getEmail())
			.regionCd(userDto.getRegionCd())
			.build();
		userDao.register(userDto);

		sendCertificationMail(userDto);
	}

	private void validateLoginIdAndEmail(UserDto userDto) {
		if (userDao.existsByLoginId(userDto.getLoginId())) {
			throw new DuplicateValueException("로그인 아이디가 이미 존재합니다.");
		}

		if (userDao.existsByEmail(userDto.getEmail())) {
			throw new DuplicateValueException("이메일 주소가 이미 존재합니다.");
		}
	}

	private void sendCertificationMail(UserDto userDto) {
		String mailToken = UUID.randomUUID().toString();
		mailService.send(MailMessage.builder()
			.loginId(userDto.getLoginId())
			.to(userDto.getEmail())
			.subject("eat-enjoy, 회원가입 인증 안내")
			.token(mailToken)
			.build());

		mailTokenDao.create(userDto.getEmail(), mailToken, MAIL_TOKEN_TIMEOUT_SECOND);
	}

	public void validateLoginIdAndPassword(LoginDto loginDto) {
		if (!userDao.existsByLoginIdAndPassword(loginDto.getLoginId(), encryptable.encrypt(loginDto.getPassword()))) {
			throw new UserNotFoundException("아이디 또는 비밀번호가 일치하지 않습니다.");
		}
	}

	@Transactional
	public void certifyEmailToken(String email, String emailToken) {
		validateEmailAndToken(email, emailToken);
		userDao.updateEmailCertified(email);
	}

	private void validateEmailAndToken(String email, String emailToken) {
		if (!userDao.existsByEmail(email)) {
			throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
		}

		if (!emailToken.equals(mailTokenDao.findByMail(email))) {
			throw new MailTokenNotFoundException("인증 토큰을 찾을 수 없습니다.");
		}
	}

	public void resendCertificationMail(String loginId) {
		UserDto userDto = userDao.findByLoginId(loginId);
		if (userDto.isCertified()) {
			throw new AlreadyCertifiedException("이미 메일 인증이 완료된 사용자 입니다.");
		}

		sendCertificationMail(UserDto.builder()
			.loginId(loginId)
			.email(userDto.getEmail())
			.build());
	}

	public UserDto findByLoginId(String loginId) {
		return userDao.findByLoginId(loginId);
	}

}
