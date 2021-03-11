package com.restaurant.eatenjoy.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.restaurant.eatenjoy.exception.AlreadyCertifiedException;
import com.restaurant.eatenjoy.exception.AuthorizationException;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.exception.NotFoundException;
import com.restaurant.eatenjoy.exception.UnauthorizedException;

import lombok.Builder;
import lombok.Getter;

@RestControllerAdvice
public class ExceptionAdvisor {

	@ExceptionHandler({ DuplicateValueException.class, AlreadyCertifiedException.class })
	public ResponseEntity<String> processBadRequestError(DuplicateValueException exception) {
		return ResponseEntity.badRequest().body(exception.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<List<InvalidField>> processValidationError(MethodArgumentNotValidException exception) {
		List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
		return ResponseEntity.badRequest().body(createInvalidFields(fieldErrors));
	}

	private List<InvalidField> createInvalidFields(List<FieldError> fieldErrors) {
		return fieldErrors.stream().map(fieldError -> InvalidField.builder().field(fieldError.getField())
			.message(fieldError.getDefaultMessage())
			.value(fieldError.getRejectedValue())
			.build())
			.collect(Collectors.toList());
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<String> processNotFoundError(NotFoundException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<String> processUnauthorizedError() {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("요청할 권한이 없습니다. 로그인 후 요청하시기 바랍니다.");
	}

	@ExceptionHandler(AuthorizationException.class)
	public ResponseEntity<String> processAuthorizationError(AuthorizationException exception) {
		String message = StringUtils.hasLength(exception.getMessage()) ? exception.getMessage() : "해당 리소스에 접근할 수 없습니다.";
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
	}

	@Getter
	@Builder
	private static class InvalidField {
		private final String field;
		private final String message;
		private final Object value;
	}

}
