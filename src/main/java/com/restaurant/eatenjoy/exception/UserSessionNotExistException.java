package com.restaurant.eatenjoy.exception;

public class UserSessionNotExistException extends RuntimeException {

	public UserSessionNotExistException(String msg) {
		super(msg);
	}
}
