package com.restaurant.eatenjoy.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class HttpResponseStatus {

	public static final ResponseEntity<HttpStatus> BAD_REQUEST = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	public static final ResponseEntity<HttpStatus> NOT_FOUND = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	public static final ResponseEntity<HttpStatus> OK = ResponseEntity.status(HttpStatus.OK).build();

}
