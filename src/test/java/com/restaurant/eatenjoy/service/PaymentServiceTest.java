package com.restaurant.eatenjoy.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.restaurant.eatenjoy.dao.PaymentDao;
import com.restaurant.eatenjoy.dto.PaymentDto;
import com.restaurant.eatenjoy.exception.IamportFailedException;
import com.restaurant.eatenjoy.exception.NoMatchedPaymentAmountException;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

	private static final Long RESERVATION_ID = 1L;

	@Mock
	private IamportClient iamportClient;

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
			.impUid("1")
			.merchantUid("1")
			.build();

		TransactionSynchronizationManager.initSynchronization();
	}

	@AfterEach
	void cleanUp() {
		TransactionSynchronizationManager.clear();
	}

	@Test
	@DisplayName("Iamport에서 결제정보 조회 실패 시 결제 정보를 등록할 수 없다.")
	void failToInsertPaymentIfIamportPaymentInfoFailed() throws IOException, IamportResponseException {
		given(iamportClient.paymentByImpUid("1")).willThrow(IamportFailedException.class);

		assertThatThrownBy(() -> paymentService.insertPayment(paymentDto))
			.isInstanceOf(IamportFailedException.class);

		then(iamportClient).should(times(1)).paymentByImpUid("1");
		then(reservationService).should(times(0)).getTotalPrice(RESERVATION_ID);
		then(paymentDao).should(times(0)).insertPayment(any());

		assertThat(TransactionSynchronizationManager.getSynchronizations().size()).isZero();
	}

	@Test
	@DisplayName("예약번호가 일치하지 않으면 결제 정보를 등록할 수 없다.")
	void failToInsertPaymentIfReservationIdIsNotMatch() throws IOException, IamportResponseException {
		given(iamportClient.paymentByImpUid("1")).willReturn(getIamportResponse("1", "2", BigDecimal.ZERO));

		assertThatThrownBy(() -> paymentService.insertPayment(paymentDto))
			.isInstanceOf(IllegalArgumentException.class);

		then(iamportClient).should(times(1)).paymentByImpUid("1");
		then(reservationService).should(times(0)).getTotalPrice(RESERVATION_ID);
		then(paymentDao).should(times(0)).insertPayment(any());

		assertThat(TransactionSynchronizationManager.getSynchronizations().size()).isZero();
	}

	@Test
	@DisplayName("결제금액이 일치하지 않으면 결제 정보를 등록할 수 없다.")
	void failToInsertPaymentIfAmountIsNotMatch() throws IOException, IamportResponseException {
		given(iamportClient.paymentByImpUid("1")).willReturn(getIamportResponse("1", "1", BigDecimal.ZERO));
		given(reservationService.getTotalPrice(RESERVATION_ID)).willReturn(BigDecimal.ONE);

		assertThatThrownBy(() -> paymentService.insertPayment(paymentDto))
			.isInstanceOf(NoMatchedPaymentAmountException.class);

		then(iamportClient).should(times(1)).paymentByImpUid("1");
		then(reservationService).should(times(1)).getTotalPrice(RESERVATION_ID);
		then(paymentDao).should(times(0)).insertPayment(any());

		assertThat(TransactionSynchronizationManager.getSynchronizations().size()).isOne();
	}

	@Test
	@DisplayName("결제 정보를 정상적으로 등록할 수 있다.")
	void successToInsertPayment() throws IOException, IamportResponseException {
		IamportResponse<Payment> iamportResponse = getIamportResponse("1", "1", BigDecimal.ONE);

		given(iamportClient.paymentByImpUid("1")).willReturn(iamportResponse);
		given(reservationService.getTotalPrice(RESERVATION_ID)).willReturn(BigDecimal.ONE);

		paymentService.insertPayment(paymentDto);

		then(iamportClient).should(times(1)).paymentByImpUid("1");
		then(reservationService).should(times(1)).getTotalPrice(RESERVATION_ID);
		then(paymentDao).should(times(1)).insertPayment(iamportResponse.getResponse());

		assertThat(TransactionSynchronizationManager.getSynchronizations().size()).isZero();
	}

	private IamportResponse<Payment> getIamportResponse(String impUid, String merchantUid, BigDecimal amount) {
		IamportResponse<Payment> iamportResponse = new IamportResponse<>();
		setFieldValue(iamportResponse, "response", getPayment(impUid, merchantUid, amount));

		return iamportResponse;
	}

	private Payment getPayment(String impUid, String merchantUid, BigDecimal amount) {
		Payment payment = new Payment();
		setFieldValue(payment, "imp_uid", impUid);
		setFieldValue(payment, "merchant_uid", merchantUid);
		setFieldValue(payment, "amount", amount);

		return payment;
	}

	private void setFieldValue(Object obj, String fieldName, Object value) {
		try {
			Field field = obj.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}