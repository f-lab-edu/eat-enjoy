package com.restaurant.eatenjoy.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.eatenjoy.dao.UserDao;
import com.restaurant.eatenjoy.dto.LoginDto;
import com.restaurant.eatenjoy.dto.UserDto;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.exception.UserNotFoundException;
import com.restaurant.eatenjoy.util.encrypt.Encryptable;
import com.restaurant.eatenjoy.util.mail.MailService;
import com.restaurant.eatenjoy.util.mail.MailMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserDao userDao;

	private final Encryptable encryptable;

	private final MailService emailService;

	@Transactional
	public void register(UserDto userDto) {
		validateLoginIdAndEmail(userDto);

		userDto = UserDto.builder()
			.loginId(userDto.getLoginId())
			.password(encryptable.encrypt(userDto.getPassword()))
			.email(userDto.getEmail())
			.emailToken(UUID.randomUUID().toString())
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
		emailService.send(MailMessage.builder()
			.loginId(userDto.getLoginId())
			.to(userDto.getEmail())
			.subject("eat-enjoy, 회원가입 인증")
			.token(userDto.getEmailToken())
			.build());
	}

	public void validateLoginIdAndPassword(LoginDto loginDto) {
		loginDto = LoginDto.builder()
			.loginId(loginDto.getLoginId())
			.password(encryptable.encrypt(loginDto.getPassword()))
			.build();

		if (!userDao.existsByLoginIdAndPassword(loginDto)) {
			throw new UserNotFoundException("아이디 또는 비밀번호가 일치하지 않습니다.");
		}
	}

	@Transactional
	public void certifyEmailToken(String email, String emailToken) {
		if (!userDao.existsByEmailAndEmailToken(email, emailToken)) {
			throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
		}

		userDao.updateEmailCertified(email);
	}

}
