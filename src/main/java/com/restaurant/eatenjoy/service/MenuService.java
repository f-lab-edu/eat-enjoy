package com.restaurant.eatenjoy.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.restaurant.eatenjoy.dao.MenuDao;
import com.restaurant.eatenjoy.dto.FileDto;
import com.restaurant.eatenjoy.dto.MenuDto;
import com.restaurant.eatenjoy.dto.MenuInfo;
import com.restaurant.eatenjoy.dto.UpdateMenuDto;
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
		try {
			menuDao.register(menuDto);
		} catch (DuplicateKeyException ex) {
			throw new DuplicateValueException(getDuplicateMenuNameMessage(menuDto.getName()), ex);
		}

		if (photo != null) {
			saveFile(menuDto.getId(), photo);
		}
	}

	public MenuInfo getMenuInfo(Long menuId) {
		return menuDao.findById(menuId);
	}

	@Transactional
	public void update(UpdateMenuDto updateMenuDto, MultipartFile photo) {
		try {
			menuDao.updateById(updateMenuDto);
		} catch (DuplicateKeyException ex) {
			throw new DuplicateValueException(getDuplicateMenuNameMessage(updateMenuDto.getName()), ex);
		}

		MenuInfo menuInfo = getMenuInfo(updateMenuDto.getId());
		if (canDeleteSavedFile(updateMenuDto.getFile(), menuInfo.getFile(), photo)) {
			deleteFile(menuInfo);
		}

		if (photo != null) {
			saveFile(updateMenuDto.getId(), photo);
		}
	}

	private void saveFile(Long menuId, MultipartFile photo) {
		FileExtension.IMAGE.validate(photo);

		FileDto fileDto = fileService.uploadFile(photo);
		Long fileId = fileService.saveFileInfo(fileDto);
		menuDao.updateFileIdById(menuId, fileId);
	}

	private void deleteFile(MenuInfo menuInfo) {
		fileService.deleteFile(menuInfo.getFile());
		fileService.deleteFileInfo(menuInfo.getFile().getId());
		menuDao.updateFileIdById(menuInfo.getId(), null);
	}

	private boolean canDeleteSavedFile(FileDto originFile, FileDto savedFile, MultipartFile photo) {
		return (originFile == null && savedFile != null) || (savedFile != null && photo != null);
	}

	private String getDuplicateMenuNameMessage(String menuName) {
		return menuName + "은(는) 이미 존재하는 메뉴명 입니다.";
	}

}
