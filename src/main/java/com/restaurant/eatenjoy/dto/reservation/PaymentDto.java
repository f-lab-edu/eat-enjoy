package com.restaurant.eatenjoy.dto.reservation;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {

	@NotBlank(message = "아임포트 거래 번호는 필수 값 입니다.")
	private String impUid;

	@NotBlank(message = "예약 번호는 필수 값 입니다.")
	private String merchantUid;

}
