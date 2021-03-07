package com.restaurant.eatenjoy.dao;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.restaurant.eatenjoy.util.mail.MailService;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MailTokenDao {

	private final StringRedisTemplate redisTemplate;

	public void create(String mail, String mailToken) {
		redisTemplate.opsForValue().set(MailService.MAIL_TOKEN_PREFIX_KEY + mail,
			mailToken, MailService.MAIL_TOKEN_TIMEOUT_SECOND);
	}

	public String findByMail(String mail) {
		return redisTemplate.opsForValue().get(MailService.MAIL_TOKEN_PREFIX_KEY + mail);
	}

}
