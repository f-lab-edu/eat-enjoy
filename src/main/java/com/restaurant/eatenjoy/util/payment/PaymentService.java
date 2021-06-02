package com.restaurant.eatenjoy.util.payment;

import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.eatenjoy.dao.PaymentDao;
import com.restaurant.eatenjoy.exception.IamportFailedException;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.Payment;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final IamportClient iamportClient;

	private final PaymentDao paymentDao;

	public Payment getPayment(String impUid) {
		try {
			return iamportClient.paymentByImpUid(impUid).getResponse();
		} catch (IamportResponseException | IOException e) {
			throw new IamportFailedException("Iamport 결제정보 조회에 실패했습니다.", e);
		}
	}

	@Transactional
	public void insert(Payment payment) {
		paymentDao.insertPayment(payment);
	}

	@Transactional
	public void updateCancelByImpUid(String impUid) {
		paymentDao.updateCancelByImpUid(getPayment(impUid));
	}

	public void cancel(String uid, boolean isImpUid) {
		cancel(new CancelData(uid, isImpUid));
	}

	public void cancel(String uid, boolean isImpUid, BigDecimal amount) {
		cancel(new CancelData(uid, isImpUid, amount));
	}

	private void cancel(CancelData cancelData) {
		try {
			iamportClient.cancelPaymentByImpUid(cancelData);
		} catch (IamportResponseException | IOException e) {
			throw new IamportFailedException("Iamport 결제취소 작업에 실패했습니다.", e);
		}
	}

}
