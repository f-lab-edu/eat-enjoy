package com.restaurant.eatenjoy.util.mail;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.restaurant.eatenjoy.util.security.Role;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Profile("default")
@Component
public class ConsoleMailService implements MailService {

	@Override
	public void send(MailMessage mailMessage) {
		log.info("Check Mail Token URL: {}", CHECK_MAIL_TOKEN_URL
			.replace("ROLE", mailMessage.getRole() == Role.USER ? "users" : "owners")
			.replace("EMAIL", mailMessage.getTo())
			.replace("TOKEN", mailMessage.getToken()));
	}

}
