package com.restaurant.eatenjoy.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.restaurant.eatenjoy.dao.MenuDao;
import com.restaurant.eatenjoy.dto.FileDto;
import com.restaurant.eatenjoy.dto.MenuDto;
import com.restaurant.eatenjoy.dto.MenuInfo;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.util.file.FileExtension;
import com.restaurant.eatenjoy.util.file.FileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuService {

	private final MenuDao menuDao;

	private final FileService fileService;

	@Transactional
	public void register(MenuDto menuDto, MultipartFile photo) {
		saveMenu(menuDto);

		if (photo == null) {
			return;
		}

		FileExtension.IMAGE.validate(photo);

		FileDto fileDto = fileService.uploadFile(photo);
		Long fileId = fileService.saveFileInfo(fileDto);
		menuDao.updateFileIdById(menuDto.getId(), fileId);
	}

	private void saveMenu(MenuDto menuDto) {
		try {
			menuDao.register(menuDto);
		} catch (DuplicateKeyException ex) {
			throw new DuplicateValueException(menuDto.getName() + "은(는) 이미 존재하는 메뉴명 입니다.", ex);
		}
	}

	public MenuInfo getMenuInfo(Long menuId) {
		return menuDao.findById(menuId);
	}

}
