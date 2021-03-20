package com.restaurant.eatenjoy.dto;

import java.time.LocalTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Value;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RestaurantDto {

	private Long id;

	@NotBlank(message = "가게 이름을 입력해주세요")
	@NotNull
	@Size(max = 100, message = "가게 이름은 100자를 넘길 수 없습니다")
	private String name;

	@NotBlank(message = "사업자 등록번호를 입력해주세요")
	@Size(min = 10, message = "사업자 등록 번호는 10글자 이상입니다")
	private String bizrNo;

	@NotBlank(message = "주소를 입력해주세요")
	private String address;

	@NotBlank(message = "지역코드를 입력해주세요.")
	@Size(max = 3, message = "최대 3자리까지 입력 가능합니다.")
	private String regionCd;

	@NotBlank(message = "가게 전화번호를 입력해주세요.")
	@Size(max = 20, message = "최대 20자리까지 입력 가능합니다.")
	private String telNo;

	@NotBlank(message = "가게 소개글을 작성해주세요")
	private String intrDc;

	private int minOrderPrice;

	@NotBlank(message = "결재 방식을 선택 해주세요")
	private String paymentType;

	private Long ownerId;

	@NotBlank(message = "매장 카테고리를 입력해 주세요")
	private Long categoryId;

	@NotBlank(message = "가게 오픈 시간을 입력해주세요")
	private LocalTime openTime;

	@NotBlank(message = "가게 마감 시간을 입력해주세요")
	private LocalTime closeTime;
}
