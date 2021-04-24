package com.restaurant.eatenjoy.util.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.restaurant.eatenjoy.dao.FileDao;
import com.restaurant.eatenjoy.dto.FileDto;
import com.restaurant.eatenjoy.exception.FileUploadFailedException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LocalFileService implements FileService {

	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

	private final FileDao fileDao;

	@Value("${file.root.path}")
	private String rootPath;

	@Override
	public FileDto uploadFile(MultipartFile multipartFile) {
		String serverFileName = UUID.randomUUID().toString();
		String subPath = LocalDate.now().format(dateFormatter);
		String uploadDirectoryPath = initializeUploadDirectoryPath(subPath);

		transferTo(multipartFile, serverFileName, uploadDirectoryPath);

		return FileDto.builder()
			.origFilename(multipartFile.getOriginalFilename())
			.serverFilename(serverFileName)
			.filePath(uploadDirectoryPath)
			.size(multipartFile.getSize())
			.build();
	}

	@Transactional
	@Override
	public Long saveFileInfo(FileDto fileDto) {
		fileDao.register(fileDto);

		return fileDto.getId();
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

	private void transferTo(MultipartFile multipartFile, String serverFileName, String uploadDirectoryPath) {
		try {
			multipartFile.transferTo(new File(uploadDirectoryPath + "\\" + serverFileName));
		} catch (IOException e) {
			throw new FileUploadFailedException("파일 업로드에 실패하였습니다.", e);
		}
	}

}
