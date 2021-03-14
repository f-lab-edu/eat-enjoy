package com.restaurant.eatenjoy.exception;

public class AuthorizationException extends RuntimeException {

	public AuthorizationException() { }

	public AuthorizationException(String message) {
		super(message);
	}

}
