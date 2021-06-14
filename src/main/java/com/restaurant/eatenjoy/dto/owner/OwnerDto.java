package com.restaurant.eatenjoy.dto.owner;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerDto {

	private Long id;

	@NotBlank(message = "아이디를 입력해주세요.")
	@Size(max = 20, message = "최대 20자리까지 입력 가능합니다.")
	private String loginId;

	@NotBlank(message = "비밀번호를 입력해주세요.")
	@Size(max = 20, message = "최대 20자리까지 입력 가능합니다.")
	private String password;

	@NotBlank(message = "이메일을 입력해주세요.")
	@Size(max = 100, message = "최대 100자리까지 입력 가능합니다.")
	@Email(message = "이메일 형식이 유효하지 않습니다.")
	private String email;

	@JsonIgnore
	private String emailToken;

	@JsonIgnore
	private boolean certified;

}
