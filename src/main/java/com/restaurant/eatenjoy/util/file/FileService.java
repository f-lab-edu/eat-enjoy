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

	/**
	 * 서브 클래스는 실제 파일 업로드를 수행하기 위해 이 메서드를 구현해야 합니다.
	 * @param multipartFile 업로드할 MultipartFile 객체
	 * @param serverFileName 업로드할 파일명(UUID)
	 * @return 업로드된 파일 경로
	 * @throws FileUploadFailedException 파일 업로드 수행 중 실패 시 발생합니다.
	 */
	protected abstract String transferTo(MultipartFile multipartFile, String serverFileName)
		throws FileUploadFailedException;

}
