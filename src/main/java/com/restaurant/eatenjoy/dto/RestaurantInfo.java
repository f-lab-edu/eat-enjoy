package com.restaurant.eatenjoy.dto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantInfo {

	private Long id;

	private Long ownerId;

	private String name;

	private String bizrNo;

	private String telNo;

	private String intrDc;

	private int minOrderPrice;

	private String paymentType;

	private LocalTime openTime;

	private LocalTime closeTime;

	private Long categoryId;

	private String postCd;

	private String baseAddress;

	private String detailAddress;

	private String sigunguCd;

	private FileDto uploadFile;
}
