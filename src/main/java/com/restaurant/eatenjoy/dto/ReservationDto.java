package com.restaurant.eatenjoy.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.restaurant.eatenjoy.util.restaurant.PaymentType;
import com.restaurant.eatenjoy.util.type.ReservationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {

	private Long id;

	@NotNull(message = "레스토랑 id는 필수 값 입니다.")
	private Long restaurantId;

	private Long userId;

	@NotNull(message = "예약일은 필수 값 입니다.")
	@FutureOrPresent
	private LocalDate reservationDate;

	@NotNull(message = "예약시간은 필수 값 입니다.")
	private LocalTime reservationTime;

	@Min(1)
	private int peopleCount;

	@NotNull(message = "결제 타입은 필수 값 입니다.")
	private PaymentType paymentType;

	@Min(0)
	private int totalPrice;

	@Valid
	private List<OrderMenuDto> orderMenus;

	private ReservationStatus status;

	public static ReservationDto createReservation(ReservationDto reservationDto, Long userId) {
		return ReservationDto.builder()
			.restaurantId(reservationDto.getRestaurantId())
			.userId(userId)
			.reservationDate(reservationDto.getReservationDate())
			.reservationTime(reservationDto.getReservationTime())
			.peopleCount(reservationDto.getPeopleCount())
			.paymentType(reservationDto.getPaymentType())
			.totalPrice(reservationDto.getTotalPrice())
			.orderMenus(reservationDto.getOrderMenus())
			.status(ReservationStatus.REQUEST)
			.build();
	}

}
