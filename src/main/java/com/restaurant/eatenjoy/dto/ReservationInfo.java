package com.restaurant.eatenjoy.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.restaurant.eatenjoy.util.type.ReservationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationInfo {

	private Long id;

	private Long restaurantId;

	private LocalDate reservationDate;

	private LocalTime reservationTime;

	private int peopleCount;

	private ReservationStatus status;

	private List<OrderMenuDto> orderMenus;

	private Payment payment;

	@Getter
	@Setter
	public static class Payment {
		private String impUid;
		private String payMethod;
		private int amount;
		private int cancelAmount;
		private String status;
		private long paidAt;
		private long cancelledAt;
	}

}
