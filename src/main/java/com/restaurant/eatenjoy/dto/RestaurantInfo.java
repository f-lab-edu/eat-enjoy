package com.restaurant.eatenjoy.dto;

import java.time.LocalTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.restaurant.eatenjoy.util.restaurant.PaymentType;

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

	private PaymentType paymentType;

	@JsonDeserialize(using = LocalTimeDeserializer.class)
	@JsonSerialize(using = LocalTimeSerializer.class)
	private LocalTime openTime;

	@JsonDeserialize(using = LocalTimeDeserializer.class)
	@JsonSerialize(using = LocalTimeSerializer.class)
	private LocalTime closeTime;

	private Long categoryId;

	private String postCd;

	private String baseAddress;

	private String detailAddress;

	private String sigunguCd;

	private String bname;

	private FileDto uploadFile;
}
