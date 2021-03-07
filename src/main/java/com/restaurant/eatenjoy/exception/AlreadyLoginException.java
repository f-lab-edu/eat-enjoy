package com.restaurant.eatenjoy.exception;

public class AlreadyLoginException extends RuntimeException {

	public AlreadyLoginException(String msg) {
		super(msg);
	}
}
