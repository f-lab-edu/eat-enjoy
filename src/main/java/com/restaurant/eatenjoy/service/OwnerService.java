package com.restaurant.eatenjoy.service;

import java.time.Duration;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.eatenjoy.dao.MailTokenDao;
import com.restaurant.eatenjoy.dao.OwnerDao;
import com.restaurant.eatenjoy.dto.LoginDto;
import com.restaurant.eatenjoy.dto.OwnerDto;
import com.restaurant.eatenjoy.exception.AlreadyCertifiedException;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.exception.MailTokenNotFoundException;
import com.restaurant.eatenjoy.exception.UserNotFoundException;
import com.restaurant.eatenjoy.util.Role;
import com.restaurant.eatenjoy.util.encrypt.Encryptable;
import com.restaurant.eatenjoy.util.mail.MailMessage;
import com.restaurant.eatenjoy.util.mail.MailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OwnerService {

	private static final Duration MAIL_TOKEN_TIMEOUT_SECOND = Duration.ofSeconds(86400);

	private final OwnerDao ownerDao;

	private final Encryptable encryptable;

	private final MailService mailService;

	private final MailTokenDao mailTokenDao;

	public void register(OwnerDto ownerDto) {
		validateLoginIdAndEmail(ownerDto);

		ownerDto = OwnerDto.builder()
			.loginId(ownerDto.getLoginId())
			.password(encryptable.encrypt(ownerDto.getPassword()))
			.email(ownerDto.getEmail())
			.build();
		ownerDao.register(ownerDto);

		sendCertificationMail(ownerDto, true);
	}

	private void validateLoginIdAndEmail(OwnerDto ownerDto) {
		if (ownerDao.existsByLoginId(ownerDto.getLoginId())) {
			throw new DuplicateValueException("로그인 아이디가 이미 존재합니다.");
		}

		if (ownerDao.existsByEmail(ownerDto.getEmail())) {
			throw new DuplicateValueException("이메일 주소가 이미 존재합니다.");
		}
	}

	private void sendCertificationMail(OwnerDto ownerDto, boolean isRegister) {
		String mailToken = UUID.randomUUID().toString();
		mailService.send(MailMessage.builder()
			.loginId(ownerDto.getLoginId())
			.to(ownerDto.getEmail())
			.subject(isRegister ? "eat-enjoy, 회원가입 인증 안내" : "eat-enjoy, 메일 인증 안내")
			.token(mailToken)
			.register(isRegister)
			.role(Role.OWNER)
			.build());

		mailTokenDao.create(Role.OWNER, ownerDto.getEmail(), mailToken, MAIL_TOKEN_TIMEOUT_SECOND);
	}

	public void validateLoginIdAndPassword(LoginDto loginDto) {
		if (!ownerDao.existsByLoginIdAndPassword(loginDto.getLoginId(), encryptable.encrypt(loginDto.getPassword()))) {
			throw new UserNotFoundException("아이디 또는 비밀번호가 일치하지 않습니다.");
		}
	}

	@Transactional
	public void certifyEmailToken(String email, String emailToken) {
		validateEmailAndToken(email, emailToken);
		ownerDao.updateEmailCertified(email);
	}

	private void validateEmailAndToken(String email, String emailToken) {
		if (!ownerDao.existsByEmail(email)) {
			throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
		}

		if (!emailToken.equals(mailTokenDao.findByRoleAndMail(Role.OWNER, email))) {
			throw new MailTokenNotFoundException("인증 토큰을 찾을 수 없습니다.");
		}
	}

	public void resendCertificationMail(String loginId) {
		OwnerDto ownerDto = ownerDao.findByLoginId(loginId);
		if (ownerDto.isCertified()) {
			throw new AlreadyCertifiedException("이미 메일 인증이 완료된 사용자 입니다.");
		}

		sendCertificationMail(OwnerDto.builder()
			.loginId(loginId)
			.email(ownerDto.getEmail())
			.build(), false);
	}

}
