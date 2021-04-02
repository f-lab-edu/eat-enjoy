package com.restaurant.eatenjoy.util.mail;

import com.restaurant.eatenjoy.util.security.Role;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MailMessage {

	private final String loginId;

	private final String to;

	private final String subject;

	private final String message;

	private final String token;

	private final boolean register;

	private final Role role;

}
