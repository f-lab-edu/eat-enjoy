package com.restaurant.eatenjoy.dao;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.restaurant.eatenjoy.util.Role;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MailTokenDao {

	private static final String MAIL_TOKEN_PREFIX_KEY = "mail:token:";

	private final StringRedisTemplate redisTemplate;

	public void create(Role role, String mail, String mailToken, Duration timeoutSecond) {
		redisTemplate.opsForValue().set(getMailTokenKey(role, mail), mailToken, timeoutSecond);
	}

	public String findByRoleAndMail(Role role, String mail) {
		return redisTemplate.opsForValue().get(getMailTokenKey(role, mail));
	}

	private String getMailTokenKey(Role role, String mail) {
		return MAIL_TOKEN_PREFIX_KEY + role + ":" + mail;
	}

}
