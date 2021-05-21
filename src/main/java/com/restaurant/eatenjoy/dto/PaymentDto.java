package com.restaurant.eatenjoy.dto;

import com.restaurant.eatenjoy.util.type.PaymentMethod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {

	private Long id;

	private PaymentMethod type;

	private int price;

	private Long reservationId;

	public static PaymentDto create(ReservationDto reservationDto) {
		return PaymentDto.builder()
			.type(reservationDto.getPaymentMethod())
			.price(reservationDto.getTotalPrice())
			.reservationId(reservationDto.getId())
			.build();
	}

}
