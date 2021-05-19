package com.restaurant.eatenjoy.util.restaurant;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentType {

	PREPAYMENT("선불"),
	POSTPAID("매장 결제"),
	FREE("자유 결제");

	private String paymentType;

	@JsonValue
	public String getPaymentType() {
		return paymentType;
	}

	PaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
}
