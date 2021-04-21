package com.restaurant.eatenjoy.util.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import com.restaurant.eatenjoy.dto.FileDto;
import com.restaurant.eatenjoy.exception.FileNotSupportException;
import com.restaurant.eatenjoy.exception.FileUploadFailedException;

public abstract class FileService {

	@Value("${file.root.path}")
	private String rootPath;

	public abstract String getSubPath();

	public FileDto upload(MultipartFile multipartFile) {
		if (!supports(multipartFile)) {
			throw new FileNotSupportException("업로드를 지원하지 않는 파일 입니다.");
		}

		String subPath = getSubPath();
		String uploadDirectoryPath = initializeUploadDirectoryPath(subPath);
		String serverFileName = UUID.randomUUID().toString();

		transferTo(multipartFile, uploadDirectoryPath, serverFileName);

		return postProcess(multipartFile, FileDto.builder()
			.origFilename(multipartFile.getOriginalFilename())
			.serverFilename(serverFileName)
			.filePath(subPath)
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

	private String initializeUploadDirectoryPath(String subPath) {
		String uploadDirectoryPath = Paths.get(getAbsoluteRootPath(), subPath).toString();
		File uploadDirectory = new File(uploadDirectoryPath);
		if (!uploadDirectory.exists()) {
			uploadDirectory.mkdir();
		}

		return uploadDirectoryPath;
	}

	private String getAbsoluteRootPath() {
		return Paths.get(System.getProperty("user.home"), rootPath).toString();
	}

	private void transferTo(MultipartFile multipartFile, String uploadDirectoryPath, String serverFileName) {
		try {
			multipartFile.transferTo(new File(uploadDirectoryPath + "\\" + serverFileName));
		} catch (IOException e) {
			throw new FileUploadFailedException(multipartFile.getOriginalFilename() + " 파일 업로드에 실패하였습니다.", e);
		}
	}

}
