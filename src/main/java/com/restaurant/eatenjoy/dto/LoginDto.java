package com.restaurant.eatenjoy.dto;

import javax.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDto {

	@NotBlank
	private String loginId;

	@NotBlank
	private String password;

	@Builder
	public LoginDto(String loginId, String password) {
		this.loginId = loginId;
		this.password = password;
	}
}
