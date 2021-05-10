package com.restaurant.eatenjoy.service;

import org.springframework.context.ApplicationEventPublisher;
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

	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public void register(MenuDto menuDto) {
		try {
			menuDao.register(menuDto);
		} catch (DuplicateKeyException ex) {
			publishFileDeleteAndThrowDuplicateValueException(menuDto.getUploadFile(), menuDto.getName(), ex);
		}
	}

	public MenuInfo getMenuInfo(Long menuId) {
		return menuDao.findById(menuId);
	}

	@Transactional
	public void update(UpdateMenuDto updateMenuDto) {
		try {
			menuDao.updateById(updateMenuDto);
		} catch (DuplicateKeyException ex) {
			publishFileDeleteAndThrowDuplicateValueException(updateMenuDto.getUploadFile(), updateMenuDto.getName(), ex);
		}

		MenuInfo menuInfo = getMenuInfo(updateMenuDto.getId());
		if (canDeleteSavedFile(updateMenuDto, menuInfo.getFile())) {
			deleteFile(menuInfo.getFile());
		}
	}

	@Transactional
	public void delete(Long menuId) {
		MenuInfo menuInfo = getMenuInfo(menuId);
		if (menuInfo.getFile() != null) {
			deleteFile(menuInfo.getFile());
		}

		menuDao.deleteById(menuId);
	}

	public FileDto uploadImage(MultipartFile photo) {
		FileExtension.IMAGE.validate(photo);

		FileDto fileDto = fileService.uploadFile(photo);
		fileService.saveFileInfo(fileDto);

		return fileDto;
	}

	private void deleteFile(FileDto fileDto) {
		fileService.deleteFile(fileDto);
		fileService.deleteFileInfo(fileDto.getId());
	}

	private boolean canDeleteSavedFile(UpdateMenuDto updateMenuDto, FileDto savedFile) {
		return (updateMenuDto.getOriginFile() == null && savedFile != null)
			|| (savedFile != null && updateMenuDto.getUploadFile() != null);
	}

	private void publishFileDeleteAndThrowDuplicateValueException(FileDto fileDto, String menuName, DuplicateKeyException cause) {
		if (fileDto != null) {
			eventPublisher.publishEvent(fileDto);
		}

		throw new DuplicateValueException(menuName + "은(는) 이미 존재하는 메뉴명 입니다.", cause);
	}

}
