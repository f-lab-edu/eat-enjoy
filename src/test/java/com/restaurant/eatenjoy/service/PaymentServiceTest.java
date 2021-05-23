package com.restaurant.eatenjoy.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.restaurant.eatenjoy.dao.PaymentDao;
import com.restaurant.eatenjoy.dto.PaymentDto;
import com.restaurant.eatenjoy.exception.NoMatchedPaymentAmountException;
import com.siot.IamportRestClient.response.Payment;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

	private static final Long RESERVATION_ID = 1L;

	@Mock
	private ReservationService reservationService;

	@Mock
	private PaymentDao paymentDao;

	@InjectMocks
	private PaymentService paymentService;

	private PaymentDto paymentDto;

	@BeforeEach
	void setUp() {
		paymentDto = PaymentDto.builder()
			.imp_uid("1")
			.merchant_uid("1")
			.build();
	}

	@Test
	@DisplayName("결제금액이 일치하지 않으면 결제 정보를 등록할 수 없다.")
	void failToInsertPaymentIfAmountIsNotMatch() {
		Payment payment = getPayment(BigDecimal.ZERO);

		given(reservationService.getTotalPrice(RESERVATION_ID)).willReturn(BigDecimal.ONE);

		assertThatThrownBy(() -> paymentService.insertPayment(paymentDto, payment))
			.isInstanceOf(NoMatchedPaymentAmountException.class);

		then(reservationService).should(times(1)).getTotalPrice(RESERVATION_ID);
		then(paymentDao).should(times(0)).insertPayment(payment);
	}

	@Test
	@DisplayName("결제 정보를 정상적으로 등록할 수 있다.")
	void successToInsertPayment() {
		Payment payment = getPayment(BigDecimal.ONE);

		given(reservationService.getTotalPrice(RESERVATION_ID)).willReturn(BigDecimal.ONE);

		paymentService.insertPayment(paymentDto, payment);

		then(reservationService).should(times(1)).getTotalPrice(RESERVATION_ID);
		then(paymentDao).should(times(1)).insertPayment(payment);
	}

	private Payment getPayment(BigDecimal amount) {
		try {
			Payment payment = new Payment();
			Field field = payment.getClass().getDeclaredField("amount");
			field.setAccessible(true);
			field.set(payment, amount);

			return payment;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}