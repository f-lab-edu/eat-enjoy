package com.restaurant.eatenjoy.dto;

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
public class UpdatePasswordDto {

	@NotBlank(message = "기존 비밀번호를 입력해주세요.")
	@Size(max = 20, message = "최대 20자리까지 입력 가능합니다.")
	private String oldPassword;

	@NotBlank(message = "신규 비밀번호를 입력해주세요.")
	@Size(max = 20, message = "최대 20자리까지 입력 가능합니다.")
	private String newPassword;

}
