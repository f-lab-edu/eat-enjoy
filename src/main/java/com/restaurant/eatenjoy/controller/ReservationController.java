package com.restaurant.eatenjoy.controller;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.eatenjoy.annotation.Authority;
import com.restaurant.eatenjoy.annotation.LoginAuthId;
import com.restaurant.eatenjoy.dto.reservation.PaymentDto;
import com.restaurant.eatenjoy.dto.reservation.ReservationDto;
import com.restaurant.eatenjoy.dto.reservation.ReservationInfo;
import com.restaurant.eatenjoy.dto.reservation.ReservationSearchDto;
import com.restaurant.eatenjoy.dto.reservation.SimpleReservationDto;
import com.restaurant.eatenjoy.service.ReservationService;
import com.restaurant.eatenjoy.util.security.Role;

import lombok.RequiredArgsConstructor;

@Authority(Role.USER)
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

	private final ReservationService reservationService;

	@GetMapping
	public List<SimpleReservationDto> reservations(@LoginAuthId Long userId,
		@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate lastReservationDate) {
		return reservationService.getReservations(ReservationSearchDto.builder()
			.userId(userId)
			.lastReservationDate(lastReservationDate)
			.build());
	}

	@GetMapping("/{reservationId}")
	public ReservationInfo reservation(@LoginAuthId Long userId, @PathVariable Long reservationId) {
		return reservationService.getReservationInfo(ReservationSearchDto.builder()
			.reservationId(reservationId)
			.userId(userId)
			.build());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Long reservation(@LoginAuthId Long userId, @RequestBody @Valid ReservationDto reservationDto) {
		return reservationService.reserve(userId, reservationDto);
	}

	@PostMapping("/payment/complete")
	@ResponseStatus(HttpStatus.CREATED)
	public void completePayment(@LoginAuthId Long userId, @RequestBody @Valid PaymentDto paymentDto) {
		reservationService.completePayment(userId, paymentDto);
	}

	@PatchMapping("/{reservationId}/cancel")
	public void cancelReservation(@LoginAuthId Long userId, @PathVariable Long reservationId) {
		reservationService.cancel(userId, reservationId);
	}

	@Authority(Role.OWNER)
	@GetMapping("/owner")
	public List<SimpleReservationDto> ownerReservations(@LoginAuthId Long ownerId,
		@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate lastReservationDate) {
		return reservationService.getReservations(ReservationSearchDto.builder()
			.ownerId(ownerId)
			.lastReservationDate(lastReservationDate)
			.build());
	}

	@Authority(Role.OWNER)
	@GetMapping("/owner/{reservationId}")
	public ReservationInfo ownerReservation(@LoginAuthId Long ownerId, @PathVariable Long reservationId) {
		return reservationService.getReservationInfo(ReservationSearchDto.builder()
			.reservationId(reservationId)
			.ownerId(ownerId)
			.build());
	}

}
