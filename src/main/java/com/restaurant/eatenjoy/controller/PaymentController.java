package com.restaurant.eatenjoy.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.eatenjoy.annotation.Authority;
import com.restaurant.eatenjoy.dto.PaymentDto;
import com.restaurant.eatenjoy.service.PaymentService;
import com.restaurant.eatenjoy.util.security.Role;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.Payment;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

	private final IamportClient iamportClient;

	private final PaymentService paymentService;

	@Authority(Role.USER)
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void payment(@RequestBody @Valid PaymentDto paymentDto) throws IOException, IamportResponseException {
		Payment payment = iamportClient.paymentByImpUid(paymentDto.getImp_uid()).getResponse();
		paymentService.insertPayment(paymentDto, payment);
	}

}
