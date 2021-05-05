package com.restaurant.eatenjoy.util.restaurant;

public enum PaymentType {

	PREPAYMENT("선불"),
	POSTPAID("매장 결제"),
	FREE("자유 결제");

	private String paymentType;

	public String getPaymentType() {
		return paymentType;
	}

	PaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
}
