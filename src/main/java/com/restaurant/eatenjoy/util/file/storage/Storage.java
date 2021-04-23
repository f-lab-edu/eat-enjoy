package com.restaurant.eatenjoy.util.file.storage;

import org.springframework.web.multipart.MultipartFile;

import com.restaurant.eatenjoy.exception.FileUploadFailedException;

public interface Storage {
	
	String transferTo(MultipartFile multipartFile, String serverFileName);
	
}
