package com.restaurant.eatenjoy.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.restaurant.eatenjoy.dao.MenuDao;
import com.restaurant.eatenjoy.dto.FileDto;
import com.restaurant.eatenjoy.dto.MenuDto;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.util.file.ImageFileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuService {

	private final MenuDao menuDao;

	private final ImageFileService fileService;

	@Transactional
	public void register(MenuDto menuDto, MultipartFile photo) {
		saveMenu(menuDto);

		if (photo != null) {
			FileDto fileDto = fileService.upload(photo);
			menuDao.updateFileIdById(menuDto.getId(), fileDto.getId());
		}
	}

	private void saveMenu(MenuDto menuDto) {
		try {
			menuDao.register(menuDto);
		} catch (DuplicateKeyException ex) {
			throw new DuplicateValueException(menuDto.getName() + "은(는) 이미 존재하는 메뉴명 입니다.", ex);
		}
	}

}
