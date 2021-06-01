package com.restaurant.eatenjoy.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.restaurant.eatenjoy.dao.ReservationDao;
import com.restaurant.eatenjoy.dto.MenuInfo;
import com.restaurant.eatenjoy.dto.OrderMenuDto;
import com.restaurant.eatenjoy.dto.ReservationDto;
import com.restaurant.eatenjoy.dto.RestaurantInfo;
import com.restaurant.eatenjoy.exception.ReservationException;
import com.restaurant.eatenjoy.util.LocalDateTimeProvider;
import com.restaurant.eatenjoy.util.restaurant.PaymentType;
import com.restaurant.eatenjoy.util.type.ReservationStatus;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

	private static final Long RESTAURANT_ID = 1L;

	private static final Long USER_ID = 1L;

	private static final LocalDate RESERVATION_DATE = LocalDate.of(2021, 5, 19);

	private static final int ORDER_MENU_COUNT = 3;

	@Mock
	private RestaurantService restaurantService;

	@Mock
	private DayCloseService dayCloseService;

	@Mock
	private PaymentService paymentService;

	@Mock
	private ReservationDao reservationDao;

	@InjectMocks
	private ReservationService reservationService;

	@BeforeEach
	void setUp() {
		LocalDateTimeProvider.mockLocalDateAt(RESERVATION_DATE);
		LocalDateTimeProvider.mockLocalTimeAt(LocalTime.of(10, 0));
	}

	@AfterEach
	void cleanUp() {
		LocalDateTimeProvider.resetClock();
	}

	@Test
	@DisplayName("예약일이 마감되었으면 예약할 수 없다.")
	void failToReservationIfReservationDateIsClose() {
		given(restaurantService.findById(RESTAURANT_ID)).willReturn(getRestaurantInfo(null));
		given(dayCloseService.isRestaurantDayClose(RESTAURANT_ID, RESERVATION_DATE)).willReturn(true);

		assertThatThrownBy(() -> reservationService.reserve(USER_ID, ReservationDto.builder()
			.restaurantId(RESTAURANT_ID)
			.reservationDate(RESERVATION_DATE)
			.build()))
			.isInstanceOf(ReservationException.class)
			.hasMessage("해당 예약일은 이미 마감되었습니다.");

		then(restaurantService).should(times(1)).findById(RESTAURANT_ID);
		then(dayCloseService).should(times(1)).isRestaurantDayClose(RESTAURANT_ID, RESERVATION_DATE);
		then(reservationDao).should(times(0)).findMenusByOrderMenus(any());
		then(reservationDao).should(times(0)).reserve(any());
		then(reservationDao).should(times(0)).insertOrderMenus(any());
	}

	@Test
	@DisplayName("레스토랑 오픈 시간보다 일찍 예약하면 예약에 실패한다.")
	void failToReservationIfReservationTimeEarlierThanRestaurantOpenTime() {
		given(restaurantService.findById(RESTAURANT_ID)).willReturn(getRestaurantInfo(null));
		given(dayCloseService.isRestaurantDayClose(RESTAURANT_ID, RESERVATION_DATE)).willReturn(false);

		assertThatThrownBy(() -> reservationService.reserve(USER_ID, ReservationDto.builder()
			.restaurantId(RESTAURANT_ID)
			.reservationDate(RESERVATION_DATE)
			.reservationTime(LocalTime.of(8, 0))
			.build()))
			.isInstanceOf(ReservationException.class)
			.hasMessage("레스토랑 오픈 시간 이전에 예약할 수 없습니다.");

		then(restaurantService).should(times(1)).findById(RESTAURANT_ID);
		then(dayCloseService).should(times(1)).isRestaurantDayClose(RESTAURANT_ID, RESERVATION_DATE);
		then(reservationDao).should(times(0)).findMenusByOrderMenus(any());
		then(reservationDao).should(times(0)).reserve(any());
		then(reservationDao).should(times(0)).insertOrderMenus(any());
	}

	@Test
	@DisplayName("레스토랑 마감 시간 1시간 전에 예약할 수 없다.")
	void failToReservationIfRestaurantCloseTime1HourBefore() {
		given(restaurantService.findById(RESTAURANT_ID)).willReturn(getRestaurantInfo(null));
		given(dayCloseService.isRestaurantDayClose(RESTAURANT_ID, RESERVATION_DATE)).willReturn(false);

		assertThatThrownBy(() -> reservationService.reserve(USER_ID, ReservationDto.builder()
			.restaurantId(RESTAURANT_ID)
			.reservationDate(RESERVATION_DATE)
			.reservationTime(LocalTime.of(19, 1))
			.build()))
			.isInstanceOf(ReservationException.class)
			.hasMessage("레스토랑 마감 시간 1시간 전까지 예약이 가능합니다.");

		then(restaurantService).should(times(1)).findById(RESTAURANT_ID);
		then(dayCloseService).should(times(1)).isRestaurantDayClose(RESTAURANT_ID, RESERVATION_DATE);
		then(reservationDao).should(times(0)).findMenusByOrderMenus(any());
		then(reservationDao).should(times(0)).reserve(any());
		then(reservationDao).should(times(0)).insertOrderMenus(any());
	}

	@Test
	@DisplayName("당일 예약이고, 예약 시간 1시간 전에 예약할 수 없다.")
	void failToReservationIfSameDayReservationAndReservationTime1HourBefore() {
		given(restaurantService.findById(RESTAURANT_ID)).willReturn(getRestaurantInfo(null));
		given(dayCloseService.isRestaurantDayClose(RESTAURANT_ID, RESERVATION_DATE)).willReturn(false);

		LocalDateTimeProvider.mockLocalTimeAt(LocalTime.of(11, 1));

		assertThatThrownBy(() -> reservationService.reserve(USER_ID, getReservationDto(PaymentType.FREE)))
			.isInstanceOf(ReservationException.class)
			.hasMessage("당일 예약일 경우 1시간 전에 예약 해야 합니다.");

		then(restaurantService).should(times(1)).findById(RESTAURANT_ID);
		then(dayCloseService).should(times(1)).isRestaurantDayClose(RESTAURANT_ID, RESERVATION_DATE);
		then(reservationDao).should(times(0)).findMenusByOrderMenus(any());
		then(reservationDao).should(times(0)).reserve(any());
		then(reservationDao).should(times(0)).insertOrderMenus(any());
	}

	@Test
	@DisplayName("결제 타입을 자유일 경우 예약할 수 없다.")
	void failToReservationIfPaymentTypeIsFree() {
		given(restaurantService.findById(RESTAURANT_ID)).willReturn(getRestaurantInfo(null));
		given(dayCloseService.isRestaurantDayClose(RESTAURANT_ID, RESERVATION_DATE)).willReturn(false);

		assertThatThrownBy(() -> reservationService.reserve(USER_ID, getReservationDto(PaymentType.FREE)))
			.isInstanceOf(ReservationException.class)
			.hasMessage("결제 방식을 선택해야 합니다.");

		then(restaurantService).should(times(1)).findById(RESTAURANT_ID);
		then(dayCloseService).should(times(1)).isRestaurantDayClose(RESTAURANT_ID, RESERVATION_DATE);
		then(reservationDao).should(times(0)).findMenusByOrderMenus(any());
		then(reservationDao).should(times(0)).reserve(any());
		then(reservationDao).should(times(0)).insertOrderMenus(any());
	}

	@Test
	@DisplayName("레스토랑 결제 타입과 일치하지 않을 경우 예약할 수 없다.")
	void failToReservationIfRestaurantPaymentTypeNotSame() {
		given(restaurantService.findById(RESTAURANT_ID)).willReturn(getRestaurantInfo(PaymentType.POSTPAID));
		given(dayCloseService.isRestaurantDayClose(RESTAURANT_ID, RESERVATION_DATE)).willReturn(false);

		assertThatThrownBy(() -> reservationService.reserve(USER_ID, getReservationDto(PaymentType.PREPAYMENT)))
			.isInstanceOf(ReservationException.class)
			.hasMessage("레스토랑 결제 방식과 일치하지 않습니다.");

		then(restaurantService).should(times(1)).findById(RESTAURANT_ID);
		then(dayCloseService).should(times(1)).isRestaurantDayClose(RESTAURANT_ID, RESERVATION_DATE);
		then(reservationDao).should(times(0)).findMenusByOrderMenus(any());
		then(reservationDao).should(times(0)).reserve(any());
		then(reservationDao).should(times(0)).insertOrderMenus(any());
	}

	@Test
	@DisplayName("매장 결제일 경우 메뉴를 선택하면 예약할 수 없다.")
	void failToReservationIfPaymentTypeIsPostPaidAndOrderMenusExists() {
		given(restaurantService.findById(RESTAURANT_ID)).willReturn(getRestaurantInfo(PaymentType.POSTPAID));
		given(dayCloseService.isRestaurantDayClose(RESTAURANT_ID, RESERVATION_DATE)).willReturn(false);

		assertThatThrownBy(() -> reservationService.reserve(USER_ID, getReservationDtoWithOrderMenus(PaymentType.POSTPAID)))
			.isInstanceOf(ReservationException.class)
			.hasMessage("매장 결제일 경우 메뉴를 선택할 수 없습니다.");

		then(restaurantService).should(times(1)).findById(RESTAURANT_ID);
		then(dayCloseService).should(times(1)).isRestaurantDayClose(RESTAURANT_ID, RESERVATION_DATE);
		then(reservationDao).should(times(0)).findMenusByOrderMenus(any());
		then(reservationDao).should(times(0)).reserve(any());
		then(reservationDao).should(times(0)).insertOrderMenus(any());
	}

	@Test
	@DisplayName("선불일 경우 메뉴를 선택하지 않으면 예약할 수 없다.")
	void failToReservationIfPaymentTypeIsPrePaidAndOrderMenusNotExists() {
		given(restaurantService.findById(RESTAURANT_ID)).willReturn(getRestaurantInfo(PaymentType.PREPAYMENT));
		given(dayCloseService.isRestaurantDayClose(RESTAURANT_ID, RESERVATION_DATE)).willReturn(false);

		assertThatThrownBy(() -> reservationService.reserve(USER_ID, getReservationDto(PaymentType.PREPAYMENT)))
			.isInstanceOf(ReservationException.class)
			.hasMessage("선결제일 경우 메뉴를 최소 하나 이상 선택해야 합니다.");

		then(restaurantService).should(times(1)).findById(RESTAURANT_ID);
		then(dayCloseService).should(times(1)).isRestaurantDayClose(RESTAURANT_ID, RESERVATION_DATE);
		then(reservationDao).should(times(0)).findMenusByOrderMenus(any());
		then(reservationDao).should(times(0)).reserve(any());
		then(reservationDao).should(times(0)).insertOrderMenus(any());
	}

	@Test
	@DisplayName("결제 타입이 선불이고 잘못된 메뉴일 경우 예약할 수 없다.")
	void failToReservationIfPaymentTypeIsPrePaidAndInvalidOrderMenus() {
		ReservationDto reservationDto = getReservationDtoWithOrderMenus(PaymentType.PREPAYMENT);

		given(restaurantService.findById(RESTAURANT_ID)).willReturn(getRestaurantInfo(PaymentType.PREPAYMENT));
		given(dayCloseService.isRestaurantDayClose(RESTAURANT_ID, RESERVATION_DATE)).willReturn(false);
		given(reservationDao.findMenusByOrderMenus(reservationDto)).willReturn(getMenuInfos(1, 0));

		assertThatThrownBy(() -> reservationService.reserve(USER_ID, reservationDto))
			.isInstanceOf(ReservationException.class)
			.hasMessage("주문 메뉴가 올바르지 않습니다.");

		then(restaurantService).should(times(1)).findById(RESTAURANT_ID);
		then(dayCloseService).should(times(1)).isRestaurantDayClose(RESTAURANT_ID, RESERVATION_DATE);
		then(reservationDao).should(times(1)).findMenusByOrderMenus(reservationDto);
		then(reservationDao).should(times(0)).reserve(any());
		then(reservationDao).should(times(0)).insertOrderMenus(any());
	}

	@Test
	@DisplayName("결제 타입이 선불이고 실제 결제 금액과 일치하지 않을 경우 예약할 수 없다.")
	void failToReservationIfPaymentTypeIsPrePaidAndRealTotalPriceNotMatch() {
		ReservationDto reservationDto = getReservationDtoWithOrderMenus(PaymentType.PREPAYMENT);

		given(restaurantService.findById(RESTAURANT_ID)).willReturn(getRestaurantInfo(PaymentType.PREPAYMENT));
		given(dayCloseService.isRestaurantDayClose(RESTAURANT_ID, RESERVATION_DATE)).willReturn(false);
		given(reservationDao.findMenusByOrderMenus(reservationDto)).willReturn(getMenuInfos(ORDER_MENU_COUNT, 3000));

		assertThatThrownBy(() -> reservationService.reserve(USER_ID, reservationDto))
			.isInstanceOf(ReservationException.class)
			.hasMessage("결제 금액이 일치하지 않습니다.");

		then(restaurantService).should(times(1)).findById(RESTAURANT_ID);
		then(dayCloseService).should(times(1)).isRestaurantDayClose(RESTAURANT_ID, RESERVATION_DATE);
		then(reservationDao).should(times(1)).findMenusByOrderMenus(reservationDto);
		then(reservationDao).should(times(0)).reserve(any());
		then(reservationDao).should(times(0)).insertOrderMenus(any());
	}

	@Test
	@DisplayName("결제 타입이 선불이고 결제 금액이 레스토랑 최소 주문 금액보다 작을 경우 예약할 수 없다.")
	void failToReservationIfPaymentTypeIsPrePaidAndRestaurantMinOrderPriceLessThan() {
		ReservationDto reservationDto = getReservationDtoWithOrderMenus(PaymentType.PREPAYMENT);

		given(restaurantService.findById(RESTAURANT_ID)).willReturn(getRestaurantInfo(PaymentType.PREPAYMENT, 20000));
		given(dayCloseService.isRestaurantDayClose(RESTAURANT_ID, RESERVATION_DATE)).willReturn(false);
		given(reservationDao.findMenusByOrderMenus(reservationDto)).willReturn(getMenuInfos(ORDER_MENU_COUNT, 5000));

		assertThatThrownBy(() -> reservationService.reserve(USER_ID, reservationDto))
			.isInstanceOf(ReservationException.class)
			.hasMessage("결제 최소 주문 금액보다 커야 합니다.");

		then(restaurantService).should(times(1)).findById(RESTAURANT_ID);
		then(dayCloseService).should(times(1)).isRestaurantDayClose(RESTAURANT_ID, RESERVATION_DATE);
		then(reservationDao).should(times(1)).findMenusByOrderMenus(reservationDto);
		then(reservationDao).should(times(0)).reserve(any());
		then(reservationDao).should(times(0)).insertOrderMenus(any());
	}

	@Test
	@DisplayName("결제 타입이 매장 결제일 경우 주문 메뉴 없이 예약 할 수 있다.")
	void successToReservationWithoutOrderMenusIfPaymentTypeIsPostPaid() {
		ReservationDto reservationDto = getReservationDto(PaymentType.POSTPAID);

		given(restaurantService.findById(RESTAURANT_ID)).willReturn(getRestaurantInfo(PaymentType.POSTPAID));
		given(dayCloseService.isRestaurantDayClose(RESTAURANT_ID, RESERVATION_DATE)).willReturn(false);

		reservationService.reserve(USER_ID, reservationDto);

		then(restaurantService).should(times(1)).findById(RESTAURANT_ID);
		then(dayCloseService).should(times(1)).isRestaurantDayClose(RESTAURANT_ID, RESERVATION_DATE);
		then(reservationDao).should(times(0)).findMenusByOrderMenus(reservationDto);
		then(reservationDao).should(times(1)).reserve(any());
		then(reservationDao).should(times(0)).insertOrderMenus(any());
	}

	@Test
	@DisplayName("결제 타입이 선불인 경우 주문 메뉴들과 함께 예약할 수 있다.")
	void successToReservationWithOrderMenusIfPaymentTypeIsPrePaid() {
		ReservationDto reservationDto = getReservationDtoWithOrderMenus(PaymentType.PREPAYMENT);

		given(restaurantService.findById(RESTAURANT_ID)).willReturn(getRestaurantInfo(PaymentType.PREPAYMENT));
		given(dayCloseService.isRestaurantDayClose(RESTAURANT_ID, RESERVATION_DATE)).willReturn(false);
		given(reservationDao.findMenusByOrderMenus(reservationDto)).willReturn(getMenuInfos(ORDER_MENU_COUNT, 5000));

		reservationService.reserve(USER_ID, reservationDto);

		then(restaurantService).should(times(1)).findById(RESTAURANT_ID);
		then(dayCloseService).should(times(1)).isRestaurantDayClose(RESTAURANT_ID, RESERVATION_DATE);
		then(reservationDao).should(times(1)).findMenusByOrderMenus(reservationDto);
		then(reservationDao).should(times(1)).reserve(any());
		then(reservationDao).should(times(1)).insertOrderMenus(reservationDto.getOrderMenus());
	}

	private ReservationDto getReservationDto(PaymentType paymentType) {
		return ReservationDto.builder()
			.restaurantId(RESTAURANT_ID)
			.userId(USER_ID)
			.reservationDate(RESERVATION_DATE)
			.reservationTime(LocalTime.of(12, 0))
			.paymentType(paymentType)
			.peopleCount(1)
			.totalPrice(15000)
			.status(ReservationStatus.REQUEST)
			.build();
	}

	private ReservationDto getReservationDtoWithOrderMenus(PaymentType paymentType) {
		return ReservationDto.builder()
			.restaurantId(RESTAURANT_ID)
			.userId(USER_ID)
			.reservationDate(RESERVATION_DATE)
			.reservationTime(LocalTime.of(12, 0))
			.paymentType(paymentType)
			.peopleCount(1)
			.totalPrice(15000)
			.orderMenus(getOrderMenus())
			.status(ReservationStatus.REQUEST)
			.build();
	}

	private List<OrderMenuDto> getOrderMenus() {
		List<OrderMenuDto> orderMenus = new ArrayList<>();
		for (long i = 1; i <= ORDER_MENU_COUNT; i++) {
			OrderMenuDto orderMenu = new OrderMenuDto();
			orderMenu.setMenuId(i);
			orderMenu.setCount(1);

			orderMenus.add(orderMenu);
		}

		return orderMenus;
	}

	private RestaurantInfo getRestaurantInfo(PaymentType paymentType) {
		return getRestaurantInfo(paymentType, 1000);
	}

	private RestaurantInfo getRestaurantInfo(PaymentType paymentType, int minOrderPrice) {
		return RestaurantInfo.builder()
			.id(RESTAURANT_ID)
			.openTime(LocalTime.of(9, 0))
			.closeTime(LocalTime.of(20, 0))
			.paymentType(paymentType)
			.minOrderPrice(minOrderPrice)
			.build();
	}

	private List<MenuInfo> getMenuInfos(int size, int price) {
		List<MenuInfo> menuInfos = new ArrayList<>();
		for (long i = 1; i <= size; i++) {
			menuInfos.add(MenuInfo.builder()
				.id(i)
				.name("메뉴" + i)
				.price(price)
				.build());
		}

		return menuInfos;
	}

}