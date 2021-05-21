package com.restaurant.eatenjoy.service;

import org.springframework.stereotype.Service;

import com.restaurant.eatenjoy.dao.PaymentDao;
import com.restaurant.eatenjoy.dto.PaymentDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DummyPaymentService implements PaymentService {

	private final PaymentDao paymentDao;

	@Override
	public void insertPayment(PaymentDto paymentDto) {
		paymentDao.insertPayment(paymentDto);
	}

}
