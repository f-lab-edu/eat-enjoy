package com.restaurant.eatenjoy.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DayCloseDto {

	@JsonIgnore
	private Long id;

	@NotNull(message = "식당 id를 입력해 주세요")
	private Long restaurantId;

	@NotNull(message = "날짜를 입력해 주세요")
	private LocalDate closeDate;
}
