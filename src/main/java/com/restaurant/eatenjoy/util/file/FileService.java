package com.restaurant.eatenjoy.util.file;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.restaurant.eatenjoy.dto.file.FileDto;

public interface FileService {

	FileDto uploadFile(MultipartFile multipartFile);

	Long saveFileInfo(FileDto fileDto);

	void deleteFile(FileDto fileDto);

	void deleteFileInfo(Long fileId);

	void deleteFiles(List<FileDto> fileDtos);

	void deleteFileInfos(List<FileDto> fileDtos);
}
