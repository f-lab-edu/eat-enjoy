package com.restaurant.eatenjoy.service;

import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.restaurant.eatenjoy.dao.PaymentDao;
import com.restaurant.eatenjoy.util.ReflectionUtils;
import com.restaurant.eatenjoy.util.payment.PaymentService;
import com.siot.IamportRestClient.response.Payment;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

	@Mock
	private PaymentDao paymentDao;

	@InjectMocks
	private PaymentService paymentService;

	@Test
	@DisplayName("결제 정보를 정상적으로 등록할 수 있다.")
	void successToInsertPayment() {
		Payment payment = getPayment("1", "1", BigDecimal.ONE);

		paymentService.insert(payment);

		then(paymentDao).should(times(1)).insertPayment(payment);
	}

	private Payment getPayment(String impUid, String merchantUid, BigDecimal amount) {
		Payment payment = new Payment();
		ReflectionUtils.setFieldValue(payment, "imp_uid", impUid);
		ReflectionUtils.setFieldValue(payment, "merchant_uid", merchantUid);
		ReflectionUtils.setFieldValue(payment, "amount", amount);

		return payment;
	}

}