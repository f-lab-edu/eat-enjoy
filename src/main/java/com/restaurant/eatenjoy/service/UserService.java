package com.restaurant.eatenjoy.service;

import java.time.Duration;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.eatenjoy.dao.MailTokenDao;
import com.restaurant.eatenjoy.dao.UserDao;
import com.restaurant.eatenjoy.dto.security.LoginDto;
import com.restaurant.eatenjoy.dto.security.UpdatePasswordDto;
import com.restaurant.eatenjoy.dto.user.UpdateUserDto;
import com.restaurant.eatenjoy.dto.user.UserDto;
import com.restaurant.eatenjoy.dto.user.UserInfoDto;
import com.restaurant.eatenjoy.exception.AlreadyCertifiedException;
import com.restaurant.eatenjoy.exception.AuthorizationException;
import com.restaurant.eatenjoy.exception.ConflictPasswordException;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.exception.MailTokenNotFoundException;
import com.restaurant.eatenjoy.exception.NoMatchedPasswordException;
import com.restaurant.eatenjoy.exception.UserNotFoundException;
import com.restaurant.eatenjoy.util.cache.CacheNames;
import com.restaurant.eatenjoy.util.security.Role;
import com.restaurant.eatenjoy.util.security.UserDetailsService;
import com.restaurant.eatenjoy.util.security.encrypt.Encryptable;
import com.restaurant.eatenjoy.util.mail.MailMessage;
import com.restaurant.eatenjoy.util.mail.MailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

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
			.build();
		userDao.register(userDto);

		sendCertificationMail(userDto, true);
	}

	@Override
	public Long findIdByLoginIdAndPassword(LoginDto loginDto) {
		return userDao.findIdByLoginIdAndPassword(loginDto.getLoginId(), encryptable.encrypt(loginDto.getPassword()));
	}

	@Cacheable(value = CacheNames.USER_MAIL_CERTIFIED, key = "#id")
	@Override
	public boolean isMailCertified(Long id) {
		UserDto userDto = userDao.findById(id);
		if (userDto == null) {
			throw new AuthorizationException();
		}

		return userDto.isCertified();
	}

	@Override
	public Role getRole() {
		return Role.USER;
	}

	@Transactional
	public void certifyEmailToken(String email, String emailToken) {
		validateEmailAndToken(email, emailToken);
		userDao.updateEmailCertified(email);
	}

	public void resendCertificationMail(Long userId) {
		UserDto userDto = userDao.findById(userId);
		if (userDto.isCertified()) {
			throw new AlreadyCertifiedException("이미 메일 인증이 완료된 사용자 입니다.");
		}

		sendCertificationMail(UserDto.builder()
			.loginId(userDto.getLoginId())
			.email(userDto.getEmail())
			.build(), false);
	}

	public UserDto findById(Long userId) {
		return userDao.findById(userId);
	}

	@CacheEvict(value = CacheNames.USER_MAIL_CERTIFIED, key = "#userId")
	@Transactional
	public void delete(Long userId, String password) {
		if (!userDao.existsByIdAndPassword(userId, encryptable.encrypt(password))) {
			throw new NoMatchedPasswordException("비밀번호가 일치하지 않습니다.");
		}

		userDao.deleteById(userId);
	}

	@Transactional
	public void updatePassword(Long userId, UpdatePasswordDto passwordDto) {
		validatePasswords(userId, passwordDto);
		userDao.updatePassword(userId, encryptable.encrypt(passwordDto.getNewPassword()));
	}

	public UserInfoDto getUserInfo(Long userId) {
		UserDto userDto = findById(userId);
		return UserInfoDto.builder()
			.id(userDto.getId())
			.loginId(userDto.getLoginId())
			.email(userDto.getEmail())
			.build();
	}

	@CacheEvict(value = CacheNames.USER_MAIL_CERTIFIED, key = "#userId")
	@Transactional
	public void update(Long userId, UpdateUserDto userDto) {
		userDao.updateById(UserDto.builder()
			.id(userId)
			.email(userDto.getEmail())
			.build());

		UserDto findUser = findById(userId);
		if (!findUser.isCertified()) {
			sendCertificationMail(findUser, false);
		}
	}

	private void validateLoginIdAndEmail(UserDto userDto) {
		if (userDao.existsByLoginId(userDto.getLoginId())) {
			throw new DuplicateValueException("로그인 아이디가 이미 존재합니다.");
		}

		if (userDao.existsByEmail(userDto.getEmail())) {
			throw new DuplicateValueException("이메일 주소가 이미 존재합니다.");
		}
	}

	private void sendCertificationMail(UserDto userDto, boolean isRegister) {
		String mailToken = UUID.randomUUID().toString();
		mailService.send(MailMessage.builder()
			.loginId(userDto.getLoginId())
			.to(userDto.getEmail())
			.subject(isRegister ? "eat-enjoy, 회원가입 인증 안내" : "eat-enjoy, 메일 인증 안내")
			.token(mailToken)
			.register(isRegister)
			.role(getRole())
			.build());

		mailTokenDao.create(getRole(), userDto.getEmail(), mailToken, MAIL_TOKEN_TIMEOUT_SECOND);
	}

	private void validateEmailAndToken(String email, String emailToken) {
		if (!userDao.existsByEmail(email)) {
			throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
		}

		if (!emailToken.equals(mailTokenDao.findByRoleAndMail(getRole(), email))) {
			throw new MailTokenNotFoundException("인증 토큰을 찾을 수 없습니다.");
		}
	}

	private void validatePasswords(Long userId, UpdatePasswordDto passwordDto) {
		if (!userDao.existsByIdAndPassword(userId, encryptable.encrypt(passwordDto.getOldPassword()))) {
			throw new NoMatchedPasswordException("기존 비밀번호가 유효하지 않습니다.");
		}

		if (passwordDto.getNewPassword().equals(passwordDto.getOldPassword())) {
			throw new ConflictPasswordException("신규 비밀번호가 기존 비밀번호와 일치합니다.");
		}
	}

}
