package com.restaurant.eatenjoy.dao;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MailTokenDao {

	private static final String MAIL_TOKEN_PREFIX_KEY = "mail:token:";

	private final StringRedisTemplate redisTemplate;

	public void create(String mail, String mailToken, Duration timeoutSecond) {
		redisTemplate.opsForValue().set(MAIL_TOKEN_PREFIX_KEY + mail, mailToken, timeoutSecond);
	}

	public String findByMail(String mail) {
		return redisTemplate.opsForValue().get(MAIL_TOKEN_PREFIX_KEY + mail);
	}

}
