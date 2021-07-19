package com.restaurant.eatenjoy.dto.reservation;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationSearchDto {

	private Long reservationId;

	private Long userId;

	private Long ownerId;

	private LocalDate lastReservationDate;

}
