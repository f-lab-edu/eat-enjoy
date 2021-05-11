package com.restaurant.eatenjoy.util.mail;

public interface MailService {

	String CHECK_MAIL_TOKEN_URL = "http://localhost:8080/api/ROLE/check-mail-token?email=EMAIL&token=TOKEN";

	void send(MailMessage mailMessage);

}
