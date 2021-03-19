package com.restaurant.eatenjoy.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailDto {

	@NotBlank(message = "이메일을 입력해주세요.")
	@Size(max = 100, message = "최대 100자리까지 입력 가능합니다.")
	@Email(message = "이메일 형식이 유효하지 않습니다.")
	private String email;

}
