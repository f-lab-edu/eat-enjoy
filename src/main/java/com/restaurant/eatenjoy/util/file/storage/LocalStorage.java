package com.restaurant.eatenjoy.util.file.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.restaurant.eatenjoy.exception.FileUploadFailedException;

@Component
public class LocalStorage implements Storage {
	
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	
	@Value("${file.root.path}")
	private String rootPath;
	
	@Override
	public String transferTo(MultipartFile multipartFile, String serverFileName) {
		String subPath = LocalDate.now().format(dateFormatter);
		String uploadDirectoryPath = initializeUploadDirectoryPath(subPath);

		try {
			multipartFile.transferTo(new File(uploadDirectoryPath + "\\" + serverFileName));
		} catch (IOException e) {
			throw new FileUploadFailedException("파일 업로드에 실패하였습니다.", e);
		}

		return subPath;
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

}
