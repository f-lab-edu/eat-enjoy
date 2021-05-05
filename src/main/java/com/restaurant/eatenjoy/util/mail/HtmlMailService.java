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
import com.restaurant.eatenjoy.util.security.Role;

import lombok.RequiredArgsConstructor;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class HtmlMailService implements MailService {

	private static final String HTML_CONTENT = initializeHtmlContent();

	private static String initializeHtmlContent() {
		ClassPathResource resource = new ClassPathResource("static/welcome.html");
		try {
			Path path = Paths.get(resource.getURI());
			return String.join("\n", Files.readAllLines(path));
		} catch (IOException e) {
			return "<!DOCTYPE html>\n<html>\n<head></head>\n<body>\n"
				+ "<div style=\"font-family: 'Apple SD Gothic Neo', 'sans-serif' !important; width: 450px; border-top: 4px solid #02b875; margin: 10px auto; box-sizing: border-box;\">"
				+ "	<h1 style=\"margin: 0; padding: 0 5px; font-size: 28px; font-weight: 400;\">"
				+ "		<span style=\"color: #02b875\">메일인증</span> 안내입니다.</h1>\n"
				+ "	<p style=\"font-size: 16px; line-height: 26px; margin-top: 50px; padding: 0 5px;\">"
				+ "		<b>LOGIN_ID</b>님 안녕하세요.<br />"
				+ "		CONTENT"
				+ "		아래 <b style=\"color: #02b875\">'메일 인증'</b> 버튼을 클릭하여 인증을 완료해 주세요.<br />"
				+ "		감사합니다.</p>"
				+ "	<a style=\"color: #FFF; text-decoration: none; text-align: center;\""
				+ "	href=\"CHECK_MAIL_TOKEN_URL\" target=\"_blank\">"
				+ "		<p style=\"display: inline-block; width: 210px; height: 45px; margin: 30px 5px 40px; background: #02b875; line-height: 45px; vertical-align: middle; font-size: 16px;\">메일 인증</p></a>"
				+ "	<div style=\"border-top: 1px solid #DDD; padding: 5px;\"></div>\n</div>\n</body>\n</html>";
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
			mimeMessageHelper.setText(HTML_CONTENT.replace("CHECK_MAIL_TOKEN_URL", CHECK_MAIL_TOKEN_URL)
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
