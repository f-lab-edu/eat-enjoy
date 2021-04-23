package com.restaurant.eatenjoy.util.file;

import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.restaurant.eatenjoy.dao.FileDao;
import com.restaurant.eatenjoy.dto.FileDto;
import com.restaurant.eatenjoy.exception.FileUploadFailedException;
import com.restaurant.eatenjoy.util.file.storage.Storage;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ImageLocalFileService extends FileService {

	private static final Set<String> includeExtensions = Set.of("jpg", "jpeg", "png", "gif", "bmp");

	private final Storage storage;
	
	private final FileDao fileDao;

	@Override
	protected boolean supports(MultipartFile multipartFile) {
		if (multipartFile == null) {
			return false;
		}

		return includeExtensions.contains(getExtension(multipartFile).toLowerCase());
	}
	
	@Override
	protected String transferTo(MultipartFile multipartFile, String serverFileName) {
		return storage.transferTo(multipartFile, serverFileName);
	}

	@Override
	protected FileDto postProcess(MultipartFile multipartFile, FileDto fileDto) {
		fileDao.register(fileDto);

		return fileDto;
	}

}
