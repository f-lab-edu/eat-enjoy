package com.restaurant.eatenjoy.util.file;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.restaurant.eatenjoy.dto.FileDto;
import com.restaurant.eatenjoy.exception.FileNotSupportException;
import com.restaurant.eatenjoy.exception.FileUploadFailedException;

public abstract class FileService {

	public FileDto upload(MultipartFile multipartFile) {
		if (!supports(multipartFile)) {
			throw new FileNotSupportException("업로드를 지원하지 않는 파일 입니다.");
		}

		String serverFileName = UUID.randomUUID().toString();
		String uploadPath = transferTo(multipartFile, serverFileName);

		return postProcess(multipartFile, FileDto.builder()
			.origFilename(multipartFile.getOriginalFilename())
			.serverFilename(serverFileName)
			.filePath(uploadPath)
			.size(multipartFile.getSize())
			.build());
	}

	protected boolean supports(MultipartFile multipartFile) {
		return (multipartFile != null);
	}

	protected FileDto postProcess(MultipartFile multipartFile, FileDto fileDto) {
		return fileDto;
	}

	protected String getExtension(MultipartFile multipartFile) {
		String fileName = multipartFile.getOriginalFilename();
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}

	protected abstract String transferTo(MultipartFile multipartFile, String serverFileName);

}
