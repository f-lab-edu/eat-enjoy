package com.restaurant.eatenjoy.util.mail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailMessage {

	private String loginId;

	private String to;

	private String subject;

	private String message;

	private String token;

}
