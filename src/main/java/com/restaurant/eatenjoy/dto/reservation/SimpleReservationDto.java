package com.restaurant.eatenjoy.dto.reservation;

import java.time.LocalDate;
import java.time.LocalTime;

import com.restaurant.eatenjoy.dto.file.FileDto;
import com.restaurant.eatenjoy.util.type.ReservationStatus;

import lombok.Getter;

@Getter
public class SimpleReservationDto {

	private Long id;

	private LocalDate reservationDate;

	private LocalTime reservationTime;

	private int peopleCount;

	private int amount;

	private ReservationStatus status;

	private Long restaurantId;

	private FileDto file;

}
