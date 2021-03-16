package com.restaurant.eatenjoy.service;

import java.time.Duration;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.restaurant.eatenjoy.dao.MailTokenDao;
import com.restaurant.eatenjoy.dao.OwnerDao;
import com.restaurant.eatenjoy.dto.OwnerDto;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
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
			.build());

		mailTokenDao.create(Role.OWNER, ownerDto.getEmail(), mailToken, MAIL_TOKEN_TIMEOUT_SECOND);
	}

}
