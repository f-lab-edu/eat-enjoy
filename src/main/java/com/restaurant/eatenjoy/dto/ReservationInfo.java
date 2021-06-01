package com.restaurant.eatenjoy.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.restaurant.eatenjoy.util.type.ReservationStatus;

import lombok.Getter;

@Getter
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
	public static class Payment {
		private String impUid;
		private String payMethod;
		private int amount;
		private String status;
		private long paidAt;
	}

}
