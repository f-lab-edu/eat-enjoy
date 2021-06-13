package com.restaurant.eatenjoy.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
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
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.restaurant.eatenjoy.dao.ReservationDao;
import com.restaurant.eatenjoy.dto.menu.MenuInfo;
import com.restaurant.eatenjoy.dto.reservation.OrderMenuDto;
import com.restaurant.eatenjoy.dto.reservation.PaymentDto;
import com.restaurant.eatenjoy.dto.reservation.ReservationDto;
import com.restaurant.eatenjoy.dto.reservation.ReservationInfo;
import com.restaurant.eatenjoy.dto.restaurant.RestaurantInfo;
import com.restaurant.eatenjoy.exception.NoMatchedPaymentAmountException;
import com.restaurant.eatenjoy.exception.NotFoundException;
import com.restaurant.eatenjoy.exception.ReservationException;
import com.restaurant.eatenjoy.util.LocalDateTimeProvider;
import com.restaurant.eatenjoy.util.ReflectionUtils;
import com.restaurant.eatenjoy.util.payment.PaymentService;
import com.restaurant.eatenjoy.util.restaurant.PaymentType;
import com.restaurant.eatenjoy.util.type.ReservationStatus;
import com.siot.IamportRestClient.response.Payment;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

	private static final Long RESERVATION_ID = 1L;

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
		TransactionSynchronizationManager.initSynchronization();
	}

	@AfterEach
	void cleanUp() {
		LocalDateTimeProvider.resetClock();
		TransactionSynchronizationManager.clear();
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

	@Test
	@DisplayName("예약 정보가 존재하지 않으면 결제를 완료할 수 없다.")
	void failToCompletePaymentIfReservationInfoIsNull() {
		given(reservationDao.findByIdAndUserId(RESERVATION_ID, USER_ID)).willReturn(null);

		assertThatThrownBy(() -> reservationService.completePayment(USER_ID, getPaymentDto()))
			.isInstanceOf(NotFoundException.class);

		then(reservationDao).should(times(1)).findByIdAndUserId(RESERVATION_ID, USER_ID);
		then(paymentService).should(times(0)).getPayment(any());
		then(paymentService).should(times(0)).insert(any());
		then(reservationDao).should(times(0)).updateStatusById(any(), any());

		assertThat(TransactionSynchronizationManager.getSynchronizations().size()).isZero();
	}

	@Test
	@DisplayName("예약 상태가 요청이 아니면 결제를 완료할 수 없다.")
	void failToCompletePaymentIfReservationStatusIsNotRequest() {
		given(reservationDao.findByIdAndUserId(RESERVATION_ID, USER_ID)).willReturn(getReservationInfo(ReservationStatus.APPROVAL, null, null));

		assertThatThrownBy(() -> reservationService.completePayment(USER_ID, getPaymentDto()))
			.isInstanceOf(ReservationException.class)
			.hasMessage("예약 요청 상태가 아닙니다.");

		then(reservationDao).should(times(1)).findByIdAndUserId(RESERVATION_ID, USER_ID);
		then(paymentService).should(times(0)).getPayment(any());
		then(paymentService).should(times(0)).insert(any());
		then(reservationDao).should(times(0)).updateStatusById(any(), any());

		assertThat(TransactionSynchronizationManager.getSynchronizations().size()).isZero();
	}

	@Test
	@DisplayName("주문 메뉴가 존재하지 않으면 결제를 완료할 수 없다.")
	void failToCompletePaymentIfOrderMenusNotExists() {
		given(reservationDao.findByIdAndUserId(RESERVATION_ID, USER_ID)).willReturn(getReservationInfo(ReservationStatus.REQUEST, new ArrayList<>(), null));

		assertThatThrownBy(() -> reservationService.completePayment(USER_ID, getPaymentDto()))
			.isInstanceOf(ReservationException.class)
			.hasMessage("주문 메뉴가 존재하지 않습니다.");

		then(reservationDao).should(times(1)).findByIdAndUserId(RESERVATION_ID, USER_ID);
		then(paymentService).should(times(0)).getPayment(any());
		then(paymentService).should(times(0)).insert(any());
		then(reservationDao).should(times(0)).updateStatusById(any(), any());

		assertThat(TransactionSynchronizationManager.getSynchronizations().size()).isZero();
	}

	@Test
	@DisplayName("결제 정보가 존재하면 결제를 완료할 수 없다.")
	void failToCompletePaymentIfPaymentInfoExists() {
		given(reservationDao.findByIdAndUserId(RESERVATION_ID, USER_ID)).willReturn(getReservationInfo(ReservationStatus.REQUEST, getOrderMenus(), new ReservationInfo.Payment()));

		assertThatThrownBy(() -> reservationService.completePayment(USER_ID, getPaymentDto()))
			.isInstanceOf(ReservationException.class)
			.hasMessage("결제 정보가 이미 존재합니다.");

		then(reservationDao).should(times(1)).findByIdAndUserId(RESERVATION_ID, USER_ID);
		then(paymentService).should(times(0)).getPayment(any());
		then(paymentService).should(times(0)).insert(any());
		then(reservationDao).should(times(0)).updateStatusById(any(), any());

		assertThat(TransactionSynchronizationManager.getSynchronizations().size()).isZero();
	}

	@Test
	@DisplayName("예약번호가 유효하지 않으면 결제를 완료할 수 없다.")
	void failToCompletePaymentIfMerchantUidIsNotValid() {
		PaymentDto paymentDto = getPaymentDto();

		given(reservationDao.findByIdAndUserId(RESERVATION_ID, USER_ID)).willReturn(getReservationInfo(ReservationStatus.REQUEST, getOrderMenus(), null));
		given(paymentService.getPayment(paymentDto.getImpUid())).willReturn(getPayment(paymentDto.getImpUid(), "2", BigDecimal.ONE));

		assertThatThrownBy(() -> reservationService.completePayment(USER_ID, paymentDto))
			.isInstanceOf(IllegalArgumentException.class);

		then(reservationDao).should(times(1)).findByIdAndUserId(RESERVATION_ID, USER_ID);
		then(paymentService).should(times(1)).getPayment(paymentDto.getImpUid());
		then(paymentService).should(times(0)).insert(any());
		then(reservationDao).should(times(0)).updateStatusById(any(), any());

		assertThat(TransactionSynchronizationManager.getSynchronizations().size()).isZero();
	}

	@Test
	@DisplayName("결제완료 상태가 아니면 결제를 완료할 수 없다.")
	void failToCompletePaymentIfPaymentStatusIsNotPaid() {
		PaymentDto paymentDto = getPaymentDto();

		given(reservationDao.findByIdAndUserId(RESERVATION_ID, USER_ID)).willReturn(getReservationInfo(ReservationStatus.REQUEST, getOrderMenus(), null));
		given(paymentService.getPayment(paymentDto.getImpUid())).willReturn(getPayment(paymentDto.getImpUid(), RESERVATION_ID.toString(), BigDecimal.ONE, "ready"));

		assertThatThrownBy(() -> reservationService.completePayment(USER_ID, paymentDto))
			.isInstanceOf(IllegalStateException.class);

		then(reservationDao).should(times(1)).findByIdAndUserId(RESERVATION_ID, USER_ID);
		then(paymentService).should(times(1)).getPayment(paymentDto.getImpUid());
		then(paymentService).should(times(0)).insert(any());
		then(reservationDao).should(times(0)).updateStatusById(any(), any());

		assertThat(TransactionSynchronizationManager.getSynchronizations().size()).isZero();
	}

	@Test
	@DisplayName("결제금액이 일치하지 않으면 결제를 완료할 수 없다.")
	void failToCompletePaymentIfPaymentAmountNotMatch() {
		PaymentDto paymentDto = getPaymentDto();

		given(reservationDao.findByIdAndUserId(RESERVATION_ID, USER_ID)).willReturn(getReservationInfo(ReservationStatus.REQUEST, getOrderMenus(), null));
		given(paymentService.getPayment(paymentDto.getImpUid())).willReturn(getPayment(paymentDto.getImpUid(), RESERVATION_ID.toString(), BigDecimal.ONE));

		assertThatThrownBy(() -> reservationService.completePayment(USER_ID, paymentDto))
			.isInstanceOf(NoMatchedPaymentAmountException.class);

		then(reservationDao).should(times(1)).findByIdAndUserId(RESERVATION_ID, USER_ID);
		then(paymentService).should(times(1)).getPayment(paymentDto.getImpUid());
		then(paymentService).should(times(0)).insert(any());
		then(reservationDao).should(times(0)).updateStatusById(any(), any());

		assertThat(TransactionSynchronizationManager.getSynchronizations().size()).isOne();
	}

	@Test
	@DisplayName("결제 정보를 정상적으로 등록하고 예약 승인상태로 변경할 수 있다.")
	void successToCompletePayment() {
		PaymentDto paymentDto = getPaymentDto();
		Payment payment = getPayment(paymentDto.getImpUid(), RESERVATION_ID.toString(), new BigDecimal(15000));

		given(reservationDao.findByIdAndUserId(RESERVATION_ID, USER_ID)).willReturn(getReservationInfo(ReservationStatus.REQUEST, getOrderMenus(), null));
		given(paymentService.getPayment(paymentDto.getImpUid())).willReturn(payment);

		reservationService.completePayment(USER_ID, paymentDto);

		then(reservationDao).should(times(1)).findByIdAndUserId(RESERVATION_ID, USER_ID);
		then(paymentService).should(times(1)).getPayment(paymentDto.getImpUid());
		then(paymentService).should(times(1)).insert(payment);
		then(reservationDao).should(times(1)).updateStatusById(RESERVATION_ID, ReservationStatus.APPROVAL);

		assertThat(TransactionSynchronizationManager.getSynchronizations().size()).isZero();
	}

	@Test
	@DisplayName("예약 정보가 존재하지 않으면 예약 취소를 할 수 없다.")
	void failToCancelReservationIfReservationInfoIsNull() {
		given(reservationDao.findByIdAndUserId(RESERVATION_ID, USER_ID)).willReturn(null);

		assertThatThrownBy(() -> reservationService.cancel(USER_ID, RESERVATION_ID))
			.isInstanceOf(NotFoundException.class);

		then(reservationDao).should(times(1)).findByIdAndUserId(RESERVATION_ID, USER_ID);
		then(reservationDao).should(times(0)).updateStatusById(any(), any());
		then(paymentService).should(times(0)).cancel(any(), eq(false), any());
		then(paymentService).should(times(0)).updateCancelByImpUid(any());
	}

	@Test
	@DisplayName("예약 상태가 승인이 아니면 예약 취소를 할 수 없다.")
	void failToCancelReservationIfReservationStatusIsNotApproval() {
		given(reservationDao.findByIdAndUserId(RESERVATION_ID, USER_ID)).willReturn(getReservationInfo(ReservationStatus.REQUEST, null, null));

		assertThatThrownBy(() -> reservationService.cancel(USER_ID, RESERVATION_ID))
			.isInstanceOf(ReservationException.class)
			.hasMessage("예약 취소가 가능한 상태가 아닙니다.");

		then(reservationDao).should(times(1)).findByIdAndUserId(RESERVATION_ID, USER_ID);
		then(reservationDao).should(times(0)).updateStatusById(any(), any());
		then(paymentService).should(times(0)).cancel(any(), eq(false), any());
		then(paymentService).should(times(0)).updateCancelByImpUid(any());
	}

	@Test
	@DisplayName("예약 일이 오늘 일자보다 이전이면 예약 취소를 할 수 없다.")
	void failToCancelReservationIfReservationDateIsEarlierThanToday() {
		given(reservationDao.findByIdAndUserId(RESERVATION_ID, USER_ID)).willReturn(getReservationInfo(LocalDate.of(2021, 5, 18), null, ReservationStatus.APPROVAL, null, null));

		assertThatThrownBy(() -> reservationService.cancel(USER_ID, RESERVATION_ID))
			.isInstanceOf(ReservationException.class)
			.hasMessage("예약 일이 오늘 일자 이전 예약 건은 취소할 수 없습니다.");

		then(reservationDao).should(times(1)).findByIdAndUserId(RESERVATION_ID, USER_ID);
		then(reservationDao).should(times(0)).updateStatusById(any(), any());
		then(paymentService).should(times(0)).cancel(any(), eq(false), any());
		then(paymentService).should(times(0)).updateCancelByImpUid(any());
	}

	@Test
	@DisplayName("결제 정보가 존재하지 않으면 예약 상태를 취소로 변경한다.")
	void successToCancelReservationIfPaymentInfoNotExists() {
		given(reservationDao.findByIdAndUserId(RESERVATION_ID, USER_ID)).willReturn(getReservationInfo(ReservationStatus.APPROVAL, getOrderMenus(), null));

		reservationService.cancel(USER_ID, RESERVATION_ID);

		then(reservationDao).should(times(1)).findByIdAndUserId(RESERVATION_ID, USER_ID);
		then(reservationDao).should(times(1)).updateStatusById(RESERVATION_ID, ReservationStatus.CANCEL);
		then(paymentService).should(times(0)).cancel(any(), eq(false), any());
		then(paymentService).should(times(0)).updateCancelByImpUid(any());
	}

	@Test
	@DisplayName("결제 정보가 존재하면 예약 상태를 취소로 변경하고 결제 취소를 할 수 있다.")
	void successToCancelReservationIfPaymentInfoExists() {
		ReservationInfo.Payment payment = new ReservationInfo.Payment();
		payment.setImpUid("1");
		payment.setAmount(15000);

		BigDecimal cancelAmount = new BigDecimal(15000);
		Payment cancelPayment = getPayment("1", "1", cancelAmount, "cancelled");

		given(reservationDao.findByIdAndUserId(RESERVATION_ID, USER_ID)).willReturn(getReservationInfo(ReservationStatus.APPROVAL, getOrderMenus(), payment));
		given(paymentService.cancel(RESERVATION_ID.toString(), false, cancelAmount)).willReturn(cancelPayment);

		reservationService.cancel(USER_ID, RESERVATION_ID);

		then(reservationDao).should(times(1)).findByIdAndUserId(RESERVATION_ID, USER_ID);
		then(reservationDao).should(times(1)).updateStatusById(RESERVATION_ID, ReservationStatus.CANCEL);
		then(paymentService).should(times(1)).cancel(RESERVATION_ID.toString(), false, cancelAmount);
		then(paymentService).should(times(1)).updateCancelByImpUid(cancelPayment);
	}

	@Test
	@DisplayName("당일 예약 한시간 전에 예약을 취소하면 결제 금액의 50% 위약금이 발생한다.")
	void successToChargePaymentAmountPenaltyIfReservationDateIsTodayAndCancelIsReservationTimeOneHourBefore() {
		ReservationInfo.Payment payment = new ReservationInfo.Payment();
		payment.setImpUid("1");
		payment.setAmount(15000);

		BigDecimal cancelAmount = new BigDecimal(7500);
		Payment cancelPayment = getPayment("1", "1", cancelAmount, "cancelled");

		given(reservationDao.findByIdAndUserId(RESERVATION_ID, USER_ID)).willReturn(getReservationInfo(RESERVATION_DATE, LocalTime.of(10, 30), ReservationStatus.APPROVAL, getOrderMenus(), payment));
		given(paymentService.cancel(RESERVATION_ID.toString(), false, cancelAmount)).willReturn(cancelPayment);

		reservationService.cancel(USER_ID, RESERVATION_ID);

		then(reservationDao).should(times(1)).findByIdAndUserId(RESERVATION_ID, USER_ID);
		then(reservationDao).should(times(1)).updateStatusById(RESERVATION_ID, ReservationStatus.CANCEL);
		then(paymentService).should(times(1)).cancel(RESERVATION_ID.toString(), false, cancelAmount);
		then(paymentService).should(times(1)).updateCancelByImpUid(cancelPayment);
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
			orderMenu.setPrice(5000);
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

	private PaymentDto getPaymentDto() {
		return PaymentDto.builder()
			.impUid("1")
			.merchantUid("1")
			.build();
	}

	private Payment getPayment(String impUid, String merchantUid, BigDecimal amount) {
		return getPayment(impUid, merchantUid, amount, "paid");
	}

	private Payment getPayment(String impUid, String merchantUid, BigDecimal amount, String status) {
		Payment payment = new Payment();
		ReflectionUtils.setFieldValue(payment, "imp_uid", impUid);
		ReflectionUtils.setFieldValue(payment, "merchant_uid", merchantUid);
		ReflectionUtils.setFieldValue(payment, "amount", amount);
		ReflectionUtils.setFieldValue(payment, "status", status);

		return payment;
	}

	private ReservationInfo getReservationInfo(ReservationStatus status, List<OrderMenuDto> orderMenus, ReservationInfo.Payment payment) {
		return getReservationInfo(RESERVATION_DATE, LocalTime.of(12, 0), status, orderMenus, payment);
	}

	private ReservationInfo getReservationInfo(LocalDate reservationDate, LocalTime reservationTime, ReservationStatus status, List<OrderMenuDto> orderMenus, ReservationInfo.Payment payment) {
		return ReservationInfo.builder()
			.id(RESERVATION_ID)
			.reservationDate(reservationDate)
			.reservationTime(reservationTime)
			.status(status)
			.orderMenus(orderMenus)
			.payment(payment)
			.build();
	}

}