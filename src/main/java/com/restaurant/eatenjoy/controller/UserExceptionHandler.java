package com.restaurant.eatenjoy.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.restaurant.eatenjoy.exception.AlreadyLoginException;
import com.restaurant.eatenjoy.exception.DuplicatedException;
import com.restaurant.eatenjoy.exception.NoUserFoundException;
import com.restaurant.eatenjoy.exception.UserSessionNotExistException;
import com.restaurant.eatenjoy.util.HttpResponseStatus;

@RestControllerAdvice(assignableTypes = UserController.class)
public class UserExceptionHandler {

	@ExceptionHandler(DuplicatedException.class)
	public ResponseEntity<HttpStatus> conflictError() {
		return HttpResponseStatus.CONFLICT;
	}

	@ExceptionHandler(AlreadyLoginException.class)
	public ResponseEntity<HttpStatus> badRequestError() {
		return HttpResponseStatus.BAD_REQUEST;
	}

	@ExceptionHandler({ NoUserFoundException.class, UserSessionNotExistException.class })
	public ResponseEntity<HttpStatus> notFoundError() {
		return HttpResponseStatus.NOT_FOUND;
	}
}
