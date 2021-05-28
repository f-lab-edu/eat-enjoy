package com.restaurant.eatenjoy.service;

import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.restaurant.eatenjoy.dao.PaymentDao;
import com.restaurant.eatenjoy.dto.PaymentDto;
import com.restaurant.eatenjoy.exception.IamportFailedException;
import com.restaurant.eatenjoy.exception.NoMatchedPaymentAmountException;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.Payment;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final IamportClient iamportClient;

	private final ReservationService reservationService;

	private final PaymentDao paymentDao;

	@Transactional
	public void insertPayment(PaymentDto paymentDto) {
		Payment payment = getIamportPayment(paymentDto.getImpUid());
		if (!paymentDto.getMerchantUid().equals(payment.getMerchantUid())) {
			throw new IllegalArgumentException("유효하지 않은 예약번호 입니다.");
		}

		BigDecimal totalPrice = reservationService.getTotalPrice(Long.parseLong(payment.getMerchantUid()));
		if (!payment.getAmount().equals(totalPrice)) {
			cancelPaymentOnRollback(payment);
			throw new NoMatchedPaymentAmountException("결제금액이 일치하지 않습니다.");
		}

		paymentDao.insertPayment(payment);
	}

	private Payment getIamportPayment(String impUid) {
		try {
			return iamportClient.paymentByImpUid(impUid).getResponse();
		} catch (IamportResponseException | IOException e) {
			throw new IamportFailedException("Iamport 결제정보 조회에 실패했습니다.", e);
		}
	}

	private void cancelPaymentOnRollback(Payment payment) {
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCompletion(int status) {
				if (status == STATUS_ROLLED_BACK) {
					reservationService.delete(Long.parseLong(payment.getMerchantUid()));

					try {
						iamportClient.cancelPaymentByImpUid(new CancelData(payment.getImpUid(), true));
					} catch (IamportResponseException | IOException e) {
						throw new IamportFailedException("Iamport 결제취소 작업에 실패했습니다.", e);
					}
				}
			}
		});
	}

}
