package com.restaurant.eatenjoy.dto.reservation;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderMenuDto {

	private Long reservationId;

	@NotNull(message = "메뉴 id는 필수 값 입니다.")
	private Long menuId;

	private String menuName;

	private int price;

	@Min(1)
	private int count;

}
