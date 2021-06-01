package com.restaurant.eatenjoy.service;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Objects;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import com.restaurant.eatenjoy.dao.RestaurantDao;
import com.restaurant.eatenjoy.dto.FileDto;
import com.restaurant.eatenjoy.dto.MenuInfo;
import com.restaurant.eatenjoy.dto.RestaurantDto;
import com.restaurant.eatenjoy.dto.RestaurantInfo;
import com.restaurant.eatenjoy.dto.RestaurantListDto;
import com.restaurant.eatenjoy.dto.UpdateRestaurant;
import com.restaurant.eatenjoy.exception.BizrNoValidException;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.exception.NotFoundException;
import com.restaurant.eatenjoy.exception.RestaurantMinOrderPriceValueException;
import com.restaurant.eatenjoy.util.BizrNoValidCheck;
import com.restaurant.eatenjoy.util.cache.CacheNames;
import com.restaurant.eatenjoy.util.file.FileExtension;
import com.restaurant.eatenjoy.util.file.FileService;
import com.restaurant.eatenjoy.util.restaurant.PaymentType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestaurantService {

	private final RestaurantDao restaurantDao;

	private final FileService fileService;

	private final MenuGroupService menuGroupService;

	private final MenuService menuService;

	@Transactional
	public void register(RestaurantDto restaurantDto, Long ownerId) {
		validatePaymentTypeAndBizrNo(restaurantDto.getPaymentType(), restaurantDto.getMinOrderPrice(),
			restaurantDto.getBizrNo());

		saveRestaurant(restaurantDto, ownerId);
	}

	private void saveRestaurant(RestaurantDto restaurantDto, Long ownerId) {
		restaurantDto = RestaurantDto.createRestaurant(restaurantDto, ownerId);

		try {
			restaurantDao.register(restaurantDto);
		} catch (DuplicateKeyException ex) {
			deleteRestaurantFileOnRollback(restaurantDto.getUploadFile());
			throw new DuplicateValueException("이미 존재하는 사업자 번호입니다", ex);
		}
	}

	public FileDto uploadImage(MultipartFile photo) {
		FileExtension.IMAGE.validate(photo);

		FileDto fileDto = fileService.uploadFile(photo);
		fileService.saveFileInfo(fileDto);

		return fileDto;
	}

	public List<RestaurantListDto> getListOfRestaurant(Long lastRestaurantId, Long ownerId) {
		return restaurantDao.findAllRestaurantList(lastRestaurantId, ownerId);
	}

	@Cacheable(value = CacheNames.RESTAURANT, key = "#id")
	public RestaurantInfo findById(Long id) {
		RestaurantInfo restaurantInfo = restaurantDao.findById(id);

		if (Objects.isNull(restaurantInfo)) {
			throw new NotFoundException("등록되어 있지 않은 식당 입니다");
		}

		return restaurantInfo;
	}

	@Transactional
	@CacheEvict(value = CacheNames.RESTAURANT, key = "#updateRestaurant.id")
	public void updateRestaurant(UpdateRestaurant updateRestaurant) {
		validatePaymentTypeAndBizrNo(updateRestaurant.getPaymentType(), updateRestaurant.getMinOrderPrice(),
			updateRestaurant.getBizrNo());

		try {
			RestaurantInfo restaurantInfo = findById(updateRestaurant.getId());

			restaurantDao.modifyRestaurantInfo(updateRestaurant);

			if (isDeleteServerFile(updateRestaurant.getUploadFile(), restaurantInfo.getUploadFile())) {
				deleteUploadFile(restaurantInfo.getUploadFile());
			}
		} catch (DuplicateKeyException ex) {
			deleteRestaurantFileOnRollback(updateRestaurant.getUploadFile());
			throw new DuplicateValueException("이미 존재하는 사업자 번호입니다", ex);
		}
	}

	@Transactional
	@CacheEvict(value = CacheNames.RESTAURANT, key = "#id")
	public void deleteRestaurant(Long id) {

		/*
		 * ToDo 예약 대기건이 존재하는지
		 * ToDo 일 마감이 존재하는지
		 * */

		List<MenuInfo> menuInfos = menuService.getMenuInfos(id);
		deleteMenuAndMenuImage(menuInfos);

		menuGroupService.deleteByRestaurantId(id);

		FileDto uploadFile = findById(id).getUploadFile();
		restaurantDao.deleteById(id);
		deleteUploadFile(uploadFile);
	}

	private void deleteMenuAndMenuImage(List<MenuInfo> menuInfos) {
		menuService.deleteByIdIn(menuInfos);

		List<FileDto> fileDtos = menuInfos.stream()
			.filter(menuInfo -> !Objects.isNull(menuInfo.getFile()))
			.map(MenuInfo::getFile)
			.collect(toList());

		deleteUploadFiles(fileDtos);
	}

	private void validatePaymentTypeAndBizrNo(PaymentType paymentType, int minOrderPrice, String bizrNo) {
		if ((PaymentType.PREPAYMENT).equals(paymentType)
			&& minOrderPrice == 0) {
			throw new RestaurantMinOrderPriceValueException("매장 결제 방식이 선불일 경우 최소 주문 가격이 0원이 될 순 없습니다");
		}

		if (!BizrNoValidCheck.valid(bizrNo)) {
			throw new BizrNoValidException("사업자 등록 번호가 잘못 되었습니다");
		}
	}

	private void deleteRestaurantFileOnRollback(FileDto fileDto) {
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCompletion(int status) {
				if (status == STATUS_ROLLED_BACK) {
					deleteUploadFile(fileDto);
				}
			}
		});
	}

	private void deleteUploadFile(FileDto fileDto) {
		fileService.deleteFile(fileDto);
		fileService.deleteFileInfo(fileDto.getId());
	}

	public void deleteUploadFiles(List<FileDto> fileDtos) {
		fileService.deleteFiles(fileDtos);
		fileService.deleteFileInfos(fileDtos);
	}

	private boolean isDeleteServerFile(FileDto restaurantImage, FileDto serverFile) {
		boolean isDelete = isDeleteUploadFile(restaurantImage, serverFile);

		return isDelete;
	}

	private boolean isDeleteUploadFile(FileDto uploadFileDto, FileDto serverFile) {
		return serverFile != null && uploadFileDto != null && !serverFile.getId().equals(uploadFileDto.getId())
			|| serverFile != null && uploadFileDto == null;
	}

}
