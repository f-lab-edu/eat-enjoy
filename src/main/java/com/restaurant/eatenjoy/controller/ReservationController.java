package com.restaurant.eatenjoy.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.eatenjoy.annotation.Authority;
import com.restaurant.eatenjoy.annotation.LoginAuthId;
import com.restaurant.eatenjoy.dto.PaymentDto;
import com.restaurant.eatenjoy.dto.ReservationDto;
import com.restaurant.eatenjoy.service.ReservationService;
import com.restaurant.eatenjoy.util.security.Role;

import lombok.RequiredArgsConstructor;

@Authority(Role.USER)
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

	private final ReservationService reservationService;

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

}
