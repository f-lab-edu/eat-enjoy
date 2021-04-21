package com.restaurant.eatenjoy.dto;

import java.time.LocalTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RestaurantInfo {

	private Long id;

	private String name;

	private String bizrNo;

	private String address;

	private String regionCd;

	private String telNo;

	private String intrDc;

	private int minOrderPrice;

	private String paymentType;

	private LocalTime openTime;

	private LocalTime closeTime;
}
