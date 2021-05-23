package com.restaurant.eatenjoy.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.restaurant.eatenjoy.dao.PaymentDao;
import com.restaurant.eatenjoy.dto.PaymentDto;
import com.restaurant.eatenjoy.exception.NoMatchedPaymentAmountException;
import com.siot.IamportRestClient.response.Payment;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final ReservationService reservationService;

	private final PaymentDao paymentDao;

	public void insertPayment(PaymentDto paymentDto, Payment payment) {
		BigDecimal totalPrice = reservationService.getTotalPrice(Long.parseLong(paymentDto.getMerchant_uid()));
		if (!payment.getAmount().equals(totalPrice)) {
			throw new NoMatchedPaymentAmountException("결제금액이 일치하지 않습니다.");
		}

		paymentDao.insertPayment(payment);
	}

}
