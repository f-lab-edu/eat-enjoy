package com.restaurant.eatenjoy.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.restaurant.eatenjoy.exception.DuplicateValueException;

import lombok.Builder;
import lombok.Getter;

@RestControllerAdvice
public class ExceptionAdvisor {

	@ExceptionHandler(DuplicateValueException.class)
	public ResponseEntity<String> processDuplicateValueError(DuplicateValueException exception) {
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

	@Getter @Builder
	private static class InvalidField {
		private final String field;
		private final String message;
		private final Object value;
	}

}
