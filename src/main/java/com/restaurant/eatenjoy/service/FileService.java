package com.restaurant.eatenjoy.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.restaurant.eatenjoy.dao.FileDao;
import com.restaurant.eatenjoy.dto.file.FileDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {

	private final FileDao fileDao;

	public Long saveFileInfo(FileDto fileDto) {
		fileDao.register(fileDto);

		return fileDto.getId();
	}

	public void deleteFileInfo(Long fileId) {
		fileDao.deleteById(fileId);
	}

	public void deleteFileInfos(List<FileDto> fileDtos) {
		fileDao.deleteByIdIn(fileDtos);
	}

}
