package com.restaurant.eatenjoy.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OwnerInfoDto {

	private final Long id;

	private final String loginId;

	private final String email;

}
