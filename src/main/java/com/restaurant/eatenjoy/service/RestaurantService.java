package com.restaurant.eatenjoy.service;

import java.util.List;
import java.util.Objects;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import com.restaurant.eatenjoy.dao.RestaurantDao;
import com.restaurant.eatenjoy.dto.FileDto;
import com.restaurant.eatenjoy.dto.RestaurantDto;
import com.restaurant.eatenjoy.dto.RestaurantInfo;
import com.restaurant.eatenjoy.dto.RestaurantListDto;
import com.restaurant.eatenjoy.dto.UpdateRestaurant;
import com.restaurant.eatenjoy.exception.BizrNoValidException;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.exception.NotFoundException;
import com.restaurant.eatenjoy.exception.RestaurantMinOrderPriceValueException;
import com.restaurant.eatenjoy.util.BizrNoValidCheck;
import com.restaurant.eatenjoy.util.file.FileExtension;
import com.restaurant.eatenjoy.util.file.FileService;
import com.restaurant.eatenjoy.util.restaurant.PaymentType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestaurantService {

	private final RestaurantDao restaurantDao;

	private final FileService fileService;

	@Transactional
	public void register(RestaurantDto restaurantDto, Long ownerId) {
		paymentTypeAndBizrNoValidCheck(restaurantDto.getPaymentType(), restaurantDto.getMinOrderPrice(),
			restaurantDto.getBizrNo());

		saveRestaurant(restaurantDto, ownerId);
	}

	private void saveRestaurant(RestaurantDto restaurantDto, Long ownerId) {
		restaurantDto = RestaurantDto.builder()
			.name(restaurantDto.getName())
			.bizrNo(restaurantDto.getBizrNo())
			.telNo(restaurantDto.getTelNo())
			.intrDc(restaurantDto.getIntrDc())
			.minOrderPrice(restaurantDto.getMinOrderPrice())
			.paymentType(restaurantDto.getPaymentType())
			.ownerId(ownerId)
			.categoryId(restaurantDto.getCategoryId())
			.openTime(restaurantDto.getOpenTime())
			.closeTime(restaurantDto.getCloseTime())
			.postCd(restaurantDto.getPostCd())
			.baseAddress(restaurantDto.getBaseAddress())
			.detailAddress(restaurantDto.getDetailAddress())
			.sigunguCd(restaurantDto.getSigunguCd())
			.uploadFile(restaurantDto.getUploadFile())
			.build();

		try {
			restaurantDao.register(restaurantDto);
		} catch (DuplicateKeyException ex) {
			restaurantFileDeleteOnRollback(restaurantDto.getUploadFile());
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

	public RestaurantInfo findById(Long id) {
		RestaurantInfo restaurantInfo = restaurantDao.findById(id);

		if (Objects.isNull(restaurantInfo)) {
			throw new NotFoundException("등록되어 있지 않은 식당 입니다");
		}

		return restaurantInfo;
	}

	@Transactional
	public void updateRestaurant(UpdateRestaurant updateRestaurant) {
		paymentTypeAndBizrNoValidCheck(updateRestaurant.getPaymentType(), updateRestaurant.getMinOrderPrice(),
			updateRestaurant.getBizrNo());

		try {
			RestaurantInfo restaurantInfo = findById(updateRestaurant.getId());

			restaurantDao.modifyRestaurantInfo(updateRestaurant);

			if (deleteUploadFileOrServerFile(updateRestaurant, restaurantInfo.getUploadFile())) {
				deleteUploadFile(restaurantInfo.getUploadFile());
			}
		} catch (DuplicateKeyException ex) {
			restaurantFileDeleteOnRollback(updateRestaurant.getUploadFile());
			throw new DuplicateValueException("이미 존재하는 사업자 번호입니다", ex);
		}
	}

	private void paymentTypeAndBizrNoValidCheck(String paymentType, int minOrderPrice, String bizrNo) {
		if ((PaymentType.PREPAYMENT.getPaymentType()).equals(paymentType)
			&& minOrderPrice == 0) {
			throw new RestaurantMinOrderPriceValueException("매장 결제 방식이 선불일 경우 최소 주문 가격이 0원이 될 순 없습니다");
		}

		if (!BizrNoValidCheck.valid(bizrNo)) {
			throw new BizrNoValidException("사업자 등록 번호가 잘못 되었습니다");
		}
	}

	private void restaurantFileDeleteOnRollback(FileDto fileDto) {
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

	private boolean deleteUploadFileOrServerFile(UpdateRestaurant updateRestaurant, FileDto serverFile) {
		boolean deleteFile = false;

		// 기존 이미지를 수정하는 경우 파일을 삭제한다
		if (serverFile != null && updateRestaurant.getUploadFile() != null) {
			deleteFile = true;

			// 기존 이미지는 그대로두고 다른 필드를 수정하는 경우, 파일을 삭제하지 않는다
			if (serverFile.getId() == updateRestaurant.getUploadFile().getId()) {
				deleteFile = false;
			}
		}

		// 식당 이미지를 삭제하는 경우
		if (serverFile != null && updateRestaurant.getUploadFile() == null) {
			deleteFile = true;
		}

		return deleteFile;
	}

}
