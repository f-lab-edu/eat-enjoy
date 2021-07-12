package com.restaurant.eatenjoy.util.mail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.restaurant.eatenjoy.exception.MailSendFailedException;
import com.restaurant.eatenjoy.exception.NotFoundException;
import com.restaurant.eatenjoy.util.security.Role;

@Profile("!default")
@Component
public class HtmlMailService implements MailService {

	private final JavaMailSender javaMailSender;

	private final String htmlContent;

	public HtmlMailService(JavaMailSender javaMailSender) {
		try {
			InputStream stream = this.getClass().getResourceAsStream("/static/welcome.html");
			htmlContent = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new NotFoundException("메일 양식을 찾을 수 없습니다.");
		}

		this.javaMailSender = javaMailSender;
	}

	@Override
	public void send(MailMessage mailMessage) {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
			mimeMessageHelper.setTo(mailMessage.getTo());
			mimeMessageHelper.setSubject(mailMessage.getSubject());
			mimeMessageHelper.setText(htmlContent.replace("CHECK_MAIL_TOKEN_URL", CHECK_MAIL_TOKEN_URL)
				.replace("LOGIN_ID", mailMessage.getLoginId())
				.replace("ROLE", mailMessage.getRole() == Role.USER ? "users" : "owners")
				.replace("EMAIL", mailMessage.getTo()).replace("TOKEN", mailMessage.getToken())
				.replace("CONTENT",
					mailMessage.isRegister() ? "<b>eat-enjoy</b>에 가입해 주셔서 진심으로 감사드립니다.<br />" : ""), true);

			javaMailSender.send(mimeMessage);
		} catch (MessagingException e) {
			throw new MailSendFailedException("메일 발송에 실패하였습니다.", e);
		}
	}

}
