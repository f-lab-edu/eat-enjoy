package com.restaurant.eatenjoy.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RestaurantListDto {

	private Long id;

	private String name;

	private String intrdc;
}
