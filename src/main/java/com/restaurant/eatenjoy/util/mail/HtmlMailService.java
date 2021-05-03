package com.restaurant.eatenjoy.util.mail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.restaurant.eatenjoy.exception.MailSendFailedException;
import com.restaurant.eatenjoy.exception.NotFoundException;
import com.restaurant.eatenjoy.util.security.Role;

import lombok.RequiredArgsConstructor;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class HtmlMailService implements MailService {

	private static final String HTML_CONTENT;

	static {
		ClassPathResource resource = new ClassPathResource("static/welcome.html");
		try {
			Path path = Paths.get(resource.getURI());
			HTML_CONTENT = String.join("\n", Files.readAllLines(path));
		} catch (IOException e) {
			throw new NotFoundException("메일 양식을 찾을 수 없습니다.");
		}
	}

	private final JavaMailSender javaMailSender;

	@Override
	public void send(MailMessage mailMessage) {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
			mimeMessageHelper.setTo(mailMessage.getTo());
			mimeMessageHelper.setSubject(mailMessage.getSubject());
			mimeMessageHelper.setText(HTML_CONTENT.replace("LOGIN_ID", mailMessage.getLoginId())
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
