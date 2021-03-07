package com.restaurant.eatenjoy.util.mail;

import java.time.Duration;

public interface MailService {

	String MAIL_TOKEN_PREFIX_KEY = "mail:token:";

	Duration MAIL_TOKEN_TIMEOUT_SECOND = Duration.ofSeconds(86400);

	void send(MailMessage mailMessage);

}
