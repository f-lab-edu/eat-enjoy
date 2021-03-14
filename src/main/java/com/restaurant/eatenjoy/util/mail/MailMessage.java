package com.restaurant.eatenjoy.util.mail;

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

}
