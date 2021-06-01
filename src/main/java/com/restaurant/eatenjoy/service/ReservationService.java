package com.restaurant.eatenjoy.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.restaurant.eatenjoy.dao.ReservationDao;
import com.restaurant.eatenjoy.dto.MenuInfo;
import com.restaurant.eatenjoy.dto.OrderMenuDto;
import com.restaurant.eatenjoy.dto.PaymentDto;
import com.restaurant.eatenjoy.dto.ReservationDto;
import com.restaurant.eatenjoy.dto.ReservationInfo;
import com.restaurant.eatenjoy.dto.RestaurantInfo;
import com.restaurant.eatenjoy.exception.NoMatchedPaymentAmountException;
import com.restaurant.eatenjoy.exception.NotFoundException;
import com.restaurant.eatenjoy.exception.ReservationException;
import com.restaurant.eatenjoy.util.LocalDateTimeProvider;
import com.restaurant.eatenjoy.util.restaurant.PaymentType;
import com.restaurant.eatenjoy.util.type.ReservationStatus;
import com.siot.IamportRestClient.response.Payment;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

	private final RestaurantService restaurantService;

	private final DayCloseService dayCloseService;

	private final PaymentService paymentService;

	private final ReservationDao reservationDao;

	@Transactional
	public Long reserve(Long userId, ReservationDto reservationDto) {
		RestaurantInfo restaurantInfo = restaurantService.findById(reservationDto.getRestaurantId());

		validateReservationDateTime(reservationDto, restaurantInfo);
		validatePaymentType(reservationDto.getPaymentType(), restaurantInfo.getPaymentType());
		List<MenuInfo> menuInfos = validateOrderMenus(reservationDto, restaurantInfo);

		reservationDto = ReservationDto.createReservation(reservationDto, userId);
		reservationDao.reserve(reservationDto);

		if (reservationDto.getPaymentType() == PaymentType.PREPAYMENT) {
			setOrderMenusInfo(reservationDto, menuInfos);
			reservationDao.insertOrderMenus(reservationDto.getOrderMenus());
		}

		return reservationDto.getId();
	}

	@Transactional
	public void completePayment(Long userId, PaymentDto paymentDto) {
		ReservationInfo reservationInfo = getReservationInfo(Long.parseLong(paymentDto.getMerchantUid()), userId);

		validateReservationBeforePaymentComplete(reservationInfo);

		Payment payment = paymentService.getIamportPayment(paymentDto.getImpUid());
		if (!paymentDto.getMerchantUid().equals(payment.getMerchantUid())) {
			throw new IllegalArgumentException("유효하지 않은 예약번호 입니다.");
		}

		validatePaymentAmount(reservationInfo, payment);

		paymentService.insert(payment);
		reservationDao.updateStatusById(reservationInfo.getId(), ReservationStatus.APPROVAL);
	}

	private void validateReservationDateTime(ReservationDto reservationDto, RestaurantInfo restaurantInfo) {
		LocalDate reservationDate = reservationDto.getReservationDate();
		if (dayCloseService.isRestaurantDayClose(restaurantInfo.getId(), reservationDate)) {
			throw new ReservationException("해당 예약일은 이미 마감되었습니다.");
		}

		LocalTime reservationTime = reservationDto.getReservationTime();
		if (reservationTime.isBefore(restaurantInfo.getOpenTime())) {
			throw new ReservationException("레스토랑 오픈 시간 이전에 예약할 수 없습니다.");
		}

		if (reservationTime.isAfter(restaurantInfo.getCloseTime().minusHours(1))) {
			throw new ReservationException("레스토랑 마감 시간 1시간 전까지 예약이 가능합니다.");
		}

		if (reservationDate.isEqual(LocalDateTimeProvider.dateOfNow())
			&& reservationTime.isBefore(LocalDateTimeProvider.timeOfNow().plusHours(1))) {
			throw new ReservationException("당일 예약일 경우 1시간 전에 예약 해야 합니다.");
		}
	}

	private void validatePaymentType(PaymentType reservationPaymentType, PaymentType restaurantPaymentType) {
		if (reservationPaymentType == PaymentType.FREE) {
			throw new ReservationException("결제 방식을 선택해야 합니다.");
		}

		if (restaurantPaymentType != PaymentType.FREE && reservationPaymentType != restaurantPaymentType) {
			throw new ReservationException("레스토랑 결제 방식과 일치하지 않습니다.");
		}
	}

	private List<MenuInfo> validateOrderMenus(ReservationDto reservationDto, RestaurantInfo restaurantInfo) {
		List<OrderMenuDto> orderMenus = reservationDto.getOrderMenus();
		boolean isPostpaid = reservationDto.getPaymentType() == PaymentType.POSTPAID;
		if (isPostpaid) {
			if (orderMenus != null && orderMenus.size() > 0) {
				throw new ReservationException("매장 결제일 경우 메뉴를 선택할 수 없습니다.");
			}

			return null;
		}

		if (orderMenus == null || orderMenus.size() == 0) {
			throw new ReservationException("선결제일 경우 메뉴를 최소 하나 이상 선택해야 합니다.");
		}

		List<MenuInfo> restaurantMenus = reservationDao.findMenusByOrderMenus(reservationDto);
		if (orderMenus.size() > restaurantMenus.size()) {
			throw new ReservationException("주문 메뉴가 올바르지 않습니다.");
		}

		int realTotalPrice = calculateRealTotalPrice(orderMenus, restaurantMenus);
		if (reservationDto.getTotalPrice() != realTotalPrice) {
			throw new ReservationException("결제 금액이 일치하지 않습니다.");
		}

		if (restaurantInfo.getMinOrderPrice() > realTotalPrice) {
			throw new ReservationException("결제 최소 주문 금액보다 커야 합니다.");
		}

		return restaurantMenus;
	}

	private int calculateRealTotalPrice(List<OrderMenuDto> orderMenus, List<MenuInfo> restaurantMenus) {
		return orderMenus.stream()
			.mapToInt(orderMenu -> {
				int price = restaurantMenus.stream()
					.filter(restaurantMenu -> orderMenu.getMenuId().equals(restaurantMenu.getId()))
					.map(MenuInfo::getPrice)
					.findFirst()
					.orElseThrow();

				return orderMenu.getCount() * price;
			})
			.sum();
	}

	private void setOrderMenusInfo(ReservationDto reservationDto, List<MenuInfo> menuInfos) {
		for (OrderMenuDto orderMenu : reservationDto.getOrderMenus()) {
			MenuInfo menu = menuInfos.stream()
				.filter(menuInfo -> orderMenu.getMenuId().equals(menuInfo.getId()))
				.findFirst()
				.orElseThrow();

			orderMenu.setReservationId(reservationDto.getId());
			orderMenu.setMenuName(menu.getName());
			orderMenu.setPrice(menu.getPrice());
		}
	}

	private ReservationInfo getReservationInfo(Long reservationId, Long userId) {
		ReservationInfo reservationInfo = reservationDao.findByIdAndUserId(reservationId, userId);
		if (reservationInfo == null) {
			throw new NotFoundException("예약 정보를 찾을 수 없습니다.");
		}

		return reservationInfo;
	}

	private void validateReservationBeforePaymentComplete(ReservationInfo reservationInfo) {
		if (reservationInfo.getStatus() != ReservationStatus.REQUEST) {
			throw new ReservationException("예약 요청 상태가 아닙니다.");
		}

		if (reservationInfo.getOrderMenus().size() == 0) {
			throw new ReservationException("주문 메뉴가 존재하지 않습니다.");
		}

		if (reservationInfo.getPayment() != null) {
			throw new ReservationException("결제 정보가 이미 존재합니다.");
		}
	}

	private void validatePaymentAmount(ReservationInfo reservationInfo, Payment payment) {
		BigDecimal totalAmount = reservationInfo.getOrderMenus().stream()
			.map(orderMenu -> new BigDecimal(orderMenu.getPrice() * orderMenu.getCount()))
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		if (payment.getAmount().compareTo(totalAmount) != 0) {
			cancelPaymentOnRollback(payment);
			throw new NoMatchedPaymentAmountException("결제금액이 일치하지 않습니다.");
		}
	}

	private void cancelPaymentOnRollback(Payment payment) {
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCompletion(int status) {
				if (status == STATUS_ROLLED_BACK) {
					reservationDao.deleteById(Long.parseLong(payment.getMerchantUid()));
					paymentService.cancel(payment);
				}
			}
		});
	}

}
