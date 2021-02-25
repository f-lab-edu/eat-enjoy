package com.restaurant.eatenjoy.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

	private Long id;

	@NotBlank(message = "아이디를 입력 해주세요")
	@Length(max = 20)
	private String loginId;

	@NotBlank(message = "비밀번호를 입력 해주세요")
	private String password;

	@NotBlank(message = "이메일을 입력 해주세요")
	@Email(message = "이메일 형식이 잘못 되었습니다")
	private String email;

	private String emailToken;
	private int certYN;

	@NotBlank(message = "지역코드를 입력 해주세요")
	@Length(max = 3)
	private String regionCD;

	private LocalDateTime insertAt;
	private LocalDateTime updateAt;

	@Builder
	public UserDto(String loginId, String password, String email, String emailToken, int certYN,
		String regionCD, LocalDateTime updateAt) {
		this.loginId = loginId;
		this.password = password;
		this.email = email;
		this.emailToken = emailToken;
		this.certYN = certYN;
		this.regionCD = regionCD;
		this.insertAt = LocalDateTime.now();
		this.updateAt = updateAt;
	}
}
