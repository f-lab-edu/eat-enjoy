package com.restaurant.eatenjoy.dao;

import com.siot.IamportRestClient.response.Payment;

public interface PaymentDao {

	void insertPayment(Payment payment);

	void updateCancelByImpUid(Payment payment);

}
