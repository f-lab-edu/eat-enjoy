package com.restaurant.eatenjoy.exception;

public class NoMatchedPasswordException extends RuntimeException {

	public NoMatchedPasswordException(String message) {
		super(message);
	}

}
