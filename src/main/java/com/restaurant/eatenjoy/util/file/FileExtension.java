package com.restaurant.eatenjoy.util.file;

import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.restaurant.eatenjoy.exception.FileNotSupportException;

public enum FileExtension {

	IMAGE(Set.of("jpg", "jpeg", "png", "gif", "bmp"));

	private final Set<String> includeExtensions;

	FileExtension(Set<String> includeExtensions) {
		this.includeExtensions = includeExtensions;
	}

	public void validate(MultipartFile multipartFile) {
		if (!isIncludeExtension(multipartFile)) {
			throw new FileNotSupportException("지원하지 않는 파일 확장자 입니다.");
		}
	}

	private boolean isIncludeExtension(MultipartFile multipartFile) {
		return includeExtensions.contains(getExtension(multipartFile).toLowerCase());
	}

	private String getExtension(MultipartFile multipartFile) {
		String fileName = multipartFile.getOriginalFilename();
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}

}
