package com.restaurant.eatenjoy.dto.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoDto {

	private final Long id;

	private final String loginId;

	private final String email;

}
