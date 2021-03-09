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
public class CustomExceptionHandler {

	@ExceptionHandler(DuplicatedException.class)
	public ResponseEntity<HttpStatus> duplicatedCreateUsersErrorHandler() {
		return HttpResponseStatus.BAD_REQUEST;
	}

	@ExceptionHandler(NoUserFoundException.class)
	public ResponseEntity<HttpStatus> noUserFoundErrorHandler() {
		return HttpResponseStatus.NOT_FOUND;
	}

	@ExceptionHandler(AlreadyLoginException.class)
	public ResponseEntity<HttpStatus> alreadyLoginErrorHandler() {
		return HttpResponseStatus.BAD_REQUEST;
	}

	@ExceptionHandler(UserSessionNotExistException.class)
	public ResponseEntity<HttpStatus> userSessionNotExistsErrorHandler() {
		return HttpResponseStatus.NOT_FOUND;
	}
}
