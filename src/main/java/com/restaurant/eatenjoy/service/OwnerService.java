package com.restaurant.eatenjoy.service;

import java.time.Duration;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.eatenjoy.dao.MailTokenDao;
import com.restaurant.eatenjoy.dao.OwnerDao;
import com.restaurant.eatenjoy.dto.LoginDto;
import com.restaurant.eatenjoy.dto.MailDto;
import com.restaurant.eatenjoy.dto.OwnerDto;
import com.restaurant.eatenjoy.dto.OwnerInfoDto;
import com.restaurant.eatenjoy.dto.UpdatePasswordDto;
import com.restaurant.eatenjoy.exception.AlreadyCertifiedException;
import com.restaurant.eatenjoy.exception.AuthorizationException;
import com.restaurant.eatenjoy.exception.ConflictPasswordException;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.exception.MailTokenNotFoundException;
import com.restaurant.eatenjoy.exception.NoMatchedPasswordException;
import com.restaurant.eatenjoy.exception.UserNotFoundException;
import com.restaurant.eatenjoy.util.security.Role;
import com.restaurant.eatenjoy.util.security.UserDetailsService;
import com.restaurant.eatenjoy.util.security.encrypt.Encryptable;
import com.restaurant.eatenjoy.util.mail.MailMessage;
import com.restaurant.eatenjoy.util.mail.MailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OwnerService implements UserDetailsService {

	private static final Duration MAIL_TOKEN_TIMEOUT_SECOND = Duration.ofSeconds(86400);

	private final OwnerDao ownerDao;

	private final Encryptable encryptable;

	private final MailService mailService;

	private final MailTokenDao mailTokenDao;

	@Transactional
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

	@Override
	public Long findIdByLoginIdAndPassword(LoginDto loginDto) {
		return ownerDao.findIdByLoginIdAndPassword(loginDto.getLoginId(), encryptable.encrypt(loginDto.getPassword()));
	}

	@Override
	public boolean isMailCertified(Long id) {
		OwnerDto ownerDto = ownerDao.findById(id);
		if (ownerDto == null) {
			throw new AuthorizationException();
		}

		return ownerDto.isCertified();
	}

	@Override
	public Role getRole() {
		return Role.OWNER;
	}

	@Transactional
	public void certifyEmailToken(String email, String emailToken) {
		validateEmailAndToken(email, emailToken);
		ownerDao.updateEmailCertified(email);
	}

	public void resendCertificationMail(Long ownerId) {
		OwnerDto ownerDto = ownerDao.findById(ownerId);
		if (ownerDto.isCertified()) {
			throw new AlreadyCertifiedException("이미 메일 인증이 완료된 사용자 입니다.");
		}

		sendCertificationMail(OwnerDto.builder()
			.loginId(ownerDto.getLoginId())
			.email(ownerDto.getEmail())
			.build(), false);
	}

	public OwnerDto findById(Long ownerId) {
		return ownerDao.findById(ownerId);
	}

	@Transactional
	public void delete(Long ownerId, String password) {
		if (!ownerDao.existsByIdAndPassword(ownerId, encryptable.encrypt(password))) {
			throw new NoMatchedPasswordException("비밀번호가 일치하지 않습니다.");
		}

		ownerDao.deleteById(ownerId);
	}

	@Transactional
	public void updatePassword(Long ownerId, UpdatePasswordDto passwordDto) {
		validatePasswords(ownerId, passwordDto);
		ownerDao.updatePassword(ownerId, encryptable.encrypt(passwordDto.getNewPassword()));
	}

	public OwnerInfoDto getOwnerInfo(Long ownerId) {
		OwnerDto ownerDto = findById(ownerId);
		return OwnerInfoDto.builder()
			.id(ownerDto.getId())
			.loginId(ownerDto.getLoginId())
			.email(ownerDto.getEmail())
			.build();
	}

	@Transactional
	public void changeMail(Long ownerId, MailDto mailDto) {
		ownerDao.updateMailById(OwnerDto.builder()
			.id(ownerId)
			.email(mailDto.getEmail())
			.build());

		OwnerDto findOwner = findById(ownerId);
		if (!findOwner.isCertified()) {
			sendCertificationMail(findOwner, false);
		}
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
			.role(getRole())
			.build());

		mailTokenDao.create(getRole(), ownerDto.getEmail(), mailToken, MAIL_TOKEN_TIMEOUT_SECOND);
	}

	private void validateEmailAndToken(String email, String emailToken) {
		if (!ownerDao.existsByEmail(email)) {
			throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
		}

		if (!emailToken.equals(mailTokenDao.findByRoleAndMail(getRole(), email))) {
			throw new MailTokenNotFoundException("인증 토큰을 찾을 수 없습니다.");
		}
	}

	private void validatePasswords(Long ownerId, UpdatePasswordDto passwordDto) {
		if (!ownerDao.existsByIdAndPassword(ownerId, encryptable.encrypt(passwordDto.getOldPassword()))) {
			throw new NoMatchedPasswordException("기존 비밀번호가 유효하지 않습니다.");
		}

		if (passwordDto.getNewPassword().equals(passwordDto.getOldPassword())) {
			throw new ConflictPasswordException("신규 비밀번호가 기존 비밀번호와 일치합니다.");
		}
	}

}
