package com.restaurant.eatenjoy.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.eatenjoy.dao.ReservationDao;
import com.restaurant.eatenjoy.dto.MenuInfo;
import com.restaurant.eatenjoy.dto.OrderMenuDto;
import com.restaurant.eatenjoy.dto.PaymentDto;
import com.restaurant.eatenjoy.dto.ReservationDto;
import com.restaurant.eatenjoy.dto.RestaurantInfo;
import com.restaurant.eatenjoy.exception.ReservationException;
import com.restaurant.eatenjoy.util.LocalDateTimeProvider;
import com.restaurant.eatenjoy.util.restaurant.PaymentType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

	private final RestaurantService restaurantService;

	private final DayCloseService dayCloseService;

	private final PaymentService paymentService;

	private final ReservationDao reservationDao;

	@Transactional
	public void reserve(Long userId, ReservationDto reservationDto) {
		RestaurantInfo restaurantInfo = restaurantService.findById(reservationDto.getRestaurantId());

		validateReservationDateTime(reservationDto, restaurantInfo);
		validatePayment(reservationDto, restaurantInfo.getPaymentType());
		List<MenuInfo> menuInfos = validateOrderMenus(reservationDto, restaurantInfo);

		reservationDto = ReservationDto.createReservation(reservationDto, userId);
		reservationDao.reserve(reservationDto);

		if (reservationDto.getPaymentType() == PaymentType.PREPAYMENT) {
			setOrderMenusInfo(reservationDto, menuInfos);
			reservationDao.insertOrderMenus(reservationDto.getOrderMenus());
			paymentService.insertPayment(PaymentDto.create(reservationDto));
		}
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

	private void validatePayment(ReservationDto reservationDto, PaymentType restaurantPaymentType) {
		PaymentType reservationPaymentType = reservationDto.getPaymentType();
		if (reservationPaymentType == PaymentType.FREE) {
			throw new ReservationException("결제 방식을 선택해야 합니다.");
		}

		if (restaurantPaymentType != PaymentType.FREE && reservationPaymentType != restaurantPaymentType) {
			throw new ReservationException("레스토랑 결제 방식과 일치하지 않습니다.");
		}

		if (reservationPaymentType == PaymentType.PREPAYMENT && reservationDto.getPaymentMethod() == null) {
			throw new ReservationException("결제 수단을 선택해야 합니다.");
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

}
