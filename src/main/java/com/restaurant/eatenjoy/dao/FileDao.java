package com.restaurant.eatenjoy.dao;

import java.util.List;

import com.restaurant.eatenjoy.dto.file.FileDto;

public interface FileDao {

	void register(FileDto fileDto);

	void deleteById(Long fileId);

	void deleteByIdIn(List<FileDto> fileDtos);
}
