package com.restaurant.eatenjoy.dto.restaurant;

import java.time.LocalDate;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DayCloseDto {

	@NotNull(message = "식당 id를 입력해 주세요")
	private Long restaurantId;

	@NotNull(message = "날짜를 입력해 주세요")
	@FutureOrPresent(message = "날짜 입력이 잘못 되었습니다")
	private LocalDate closeDate;
}
