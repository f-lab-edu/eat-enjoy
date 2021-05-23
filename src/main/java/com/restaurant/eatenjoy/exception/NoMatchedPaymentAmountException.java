package com.restaurant.eatenjoy.exception;

public class NoMatchedPaymentAmountException extends RuntimeException {

	public NoMatchedPaymentAmountException(String message) {
		super(message);
	}

}
