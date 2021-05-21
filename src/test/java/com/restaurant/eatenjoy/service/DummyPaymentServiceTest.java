package com.restaurant.eatenjoy.service;

import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.restaurant.eatenjoy.dao.PaymentDao;
import com.restaurant.eatenjoy.dto.PaymentDto;
import com.restaurant.eatenjoy.util.type.PaymentMethod;

@ExtendWith(MockitoExtension.class)
class DummyPaymentServiceTest {

	@Mock
	private PaymentDao paymentDao;

	@InjectMocks
	private DummyPaymentService paymentService;

	@Test
	@DisplayName("가상 결제정보를 정상적으로 등록할 수 있다.")
	void successToPayment() {
		PaymentDto paymentDto = PaymentDto.builder()
			.type(PaymentMethod.CARD)
			.price(10000)
			.reservationId(1L)
			.build();

		paymentService.insertPayment(paymentDto);

		then(paymentDao).should(times(1)).insertPayment(paymentDto);
	}

}