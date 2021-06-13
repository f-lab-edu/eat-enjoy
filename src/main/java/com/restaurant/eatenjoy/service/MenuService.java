package com.restaurant.eatenjoy.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import com.restaurant.eatenjoy.dao.MenuDao;
import com.restaurant.eatenjoy.dto.file.FileDto;
import com.restaurant.eatenjoy.dto.menu.MenuDto;
import com.restaurant.eatenjoy.dto.menu.MenuInfo;
import com.restaurant.eatenjoy.dto.menu.UpdateMenuDto;
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
	public void register(Long restaurantId, MenuDto menuDto) {
		validateMenuName(restaurantId, null, menuDto.getName(), menuDto.getUploadFile());

		menuDao.register(menuDto);
	}

	public MenuInfo getMenuInfo(Long menuId) {
		return menuDao.findById(menuId);
	}

	public List<MenuInfo> getMenuInfos(Long restaurantId) {
		return menuDao.findAllByRestaurantId(restaurantId);
	}

	@Transactional
	public void update(Long restaurantId, UpdateMenuDto updateMenuDto) {
		validateMenuName(restaurantId, updateMenuDto.getId(), updateMenuDto.getName(), updateMenuDto.getUploadFile());

		MenuInfo menuInfo = getMenuInfo(updateMenuDto.getId());
		menuDao.updateById(updateMenuDto);

		if (canDeleteSavedFile(updateMenuDto, menuInfo.getFile())) {
			deleteFile(menuInfo.getFile());
		}
	}

	@Transactional
	public void delete(Long menuId) {
		MenuInfo menuInfo = getMenuInfo(menuId);
		menuDao.deleteById(menuId);

		if (menuInfo.getFile() != null) {
			deleteFile(menuInfo.getFile());
		}
	}

	public void deleteByIdIn(List<MenuInfo> menus) {
		menuDao.deleteByIdIn(menus);
	}

	public FileDto uploadImage(MultipartFile photo) {
		FileExtension.IMAGE.validate(photo);

		FileDto fileDto = fileService.uploadFile(photo);
		fileService.saveFileInfo(fileDto);

		return fileDto;
	}

	private void validateMenuName(Long restaurantId, Long menuId, String menuName, FileDto fileDto) {
		if (!menuDao.existsByRestaurantIdAndName(restaurantId, menuId, menuName)) {
			return;
		}

		if (fileDto != null) {
			deleteUploadFileOnRollback(fileDto);
		}

		throw new DuplicateValueException(menuName + "은(는) 이미 존재하는 메뉴명 입니다.");
	}

	private void deleteUploadFileOnRollback(FileDto fileDto) {
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCompletion(int status) {
				if (status == STATUS_ROLLED_BACK) {
					deleteFile(fileDto);
				}
			}
		});
	}

	private void deleteFile(FileDto fileDto) {
		fileService.deleteFile(fileDto);
		fileService.deleteFileInfo(fileDto.getId());
	}

	private boolean canDeleteSavedFile(UpdateMenuDto updateMenuDto, FileDto savedFile) {
		return (updateMenuDto.getOriginFile() == null && savedFile != null)
			|| (savedFile != null && updateMenuDto.getUploadFile() != null);
	}

}
