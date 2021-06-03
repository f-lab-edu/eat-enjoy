package com.restaurant.eatenjoy.util.payment;

import com.siot.IamportRestClient.response.Payment;

public enum PaymentStatus {

	READY, PAID, CANCELLED, FAILED;

	public boolean isMatch(Payment payment) {
		return this.name().equalsIgnoreCase(payment.getStatus());
	}

}
