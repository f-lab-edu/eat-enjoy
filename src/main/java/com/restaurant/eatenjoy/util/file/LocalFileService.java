package com.restaurant.eatenjoy.util.file;

import java.io.File;
import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.restaurant.eatenjoy.dao.FileDao;
import com.restaurant.eatenjoy.dto.FileDto;
import com.restaurant.eatenjoy.exception.FileNotFoundException;
import com.restaurant.eatenjoy.exception.FileUploadFailedException;

import lombok.RequiredArgsConstructor;

@Profile("default")
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

		transferTo(multipartFile, uploadDirectoryPath, serverFileName);

		return FileDto.builder()
			.origFilename(multipartFile.getOriginalFilename())
			.serverFilename(serverFileName)
			.filePath(subPath)
			.size(multipartFile.getSize())
			.build();
	}

	@Override
	public Long saveFileInfo(FileDto fileDto) {
		fileDao.register(fileDto);

		return fileDto.getId();
	}

	@Override
	public void deleteFile(FileDto fileDto) {
		Path file = Path.of(getRealServerFilePath(fileDto.getFilePath(), fileDto.getServerFilename()));

		try(AsynchronousFileChannel open = AsynchronousFileChannel.open(file, StandardOpenOption.DELETE_ON_CLOSE)) {
		} catch (IOException ex) {
			throw new FileNotFoundException("파일을 찾을 수 없습니다");
		}
	}

	@Override
	public void deleteFileInfo(Long fileId) {
		fileDao.deleteById(fileId);
	}

	@Override
	public void deleteFiles(List<FileDto> fileDtos) {
		fileDtos.forEach(this::deleteFile);
	}

	@Override
	public void deleteFileInfos(List<FileDto> fileDtos) {
		fileDao.deleteByIdIn(fileDtos);
	}

	private String initializeUploadDirectoryPath(String subPath) {
		String uploadDirectoryPath = getUploadDirectoryPath(subPath);
		File uploadDirectory = new File(uploadDirectoryPath);
		if (!uploadDirectory.exists()) {
			uploadDirectory.mkdir();
		}

		return uploadDirectoryPath;
	}

	private void transferTo(MultipartFile multipartFile, String uploadDirectoryPath, String serverFileName) {
		try {
			multipartFile.transferTo(new File(uploadDirectoryPath + "\\" + serverFileName));
		} catch (IOException e) {
			throw new FileUploadFailedException("파일 업로드에 실패하였습니다.", e);
		}
	}

	private String getAbsoluteRootPath() {
		return Paths.get(System.getProperty("user.home"), rootPath).toString();
	}

	private String getUploadDirectoryPath(String subPath) {
		return Paths.get(getAbsoluteRootPath(), subPath).toString();
	}

	private String getRealServerFilePath(String subPath, String serverFileName) {
		return Paths.get(getUploadDirectoryPath(subPath), serverFileName).toString();
	}

}
