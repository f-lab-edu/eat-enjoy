package com.restaurant.eatenjoy.util.file;

import org.springframework.web.multipart.MultipartFile;

import com.restaurant.eatenjoy.dto.FileDto;

public interface FileService {

	FileDto uploadFile(MultipartFile multipartFile);

	Long saveFileInfo(FileDto fileDto);

}
