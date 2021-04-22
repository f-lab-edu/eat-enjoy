package com.restaurant.eatenjoy.util.file;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.restaurant.eatenjoy.dao.FileDao;
import com.restaurant.eatenjoy.dto.FileDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ImageLocalFileService extends LocalFileService {

	private static final Set<String> includeExtensions = Set.of("jpg", "jpeg", "png", "gif", "bmp");

	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

	private final FileDao fileDao;

	@Override
	protected boolean supports(MultipartFile multipartFile) {
		if (multipartFile == null) {
			return false;
		}

		return includeExtensions.contains(getExtension(multipartFile).toLowerCase());
	}

	@Override
	public String getSubPath() {
		return LocalDate.now().format(dateFormatter);
	}

	@Override
	protected FileDto postProcess(MultipartFile multipartFile, FileDto fileDto) {
		fileDao.register(fileDto);

		return fileDto;
	}

}
