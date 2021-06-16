package com.restaurant.eatenjoy.util.file;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.restaurant.eatenjoy.dto.file.FileDto;

public interface FileManager {

	FileDto uploadFile(Long restaurantId, MultipartFile multipartFile);

	void deleteFile(FileDto fileDto);

	void deleteFiles(List<FileDto> fileDtos);

}
