package com.restaurant.eatenjoy.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import com.restaurant.eatenjoy.dao.DayCloseDao;
import com.restaurant.eatenjoy.dto.restaurant.DayCloseDto;
import com.restaurant.eatenjoy.exception.DuplicateValueException;

@ExtendWith(MockitoExtension.class)
class DayCloseServiceTest {

	@Mock
	private DayCloseDao dayCloseDao;

	@InjectMocks
	private DayCloseService dayCloseService;

	private DayCloseDto getDayCloseDto() {
		return DayCloseDto.builder()
			.restaurantId(1L)
			.closeDate(LocalDate.now())
			.build();
	}

	@Test
	@DisplayName("일 마감 등록 성공")
	void successDayClose() {
		// given
		DayCloseDto dayCloseDto = getDayCloseDto();

		// when
		dayCloseService.register(dayCloseDto);

		// then
		then(dayCloseDao).should(times(1)).register(dayCloseDto);
	}

	@Test
	@DisplayName("일 마감 등록 실패 - 이미 처리된 일 마감 처리된 식당")
	void failRegisterDayClose() {
		// given
		DayCloseDto dayCloseDto = getDayCloseDto();

		// when
		doThrow(DuplicateKeyException.class).when(dayCloseDao).register(dayCloseDto);
		assertThatThrownBy(() -> dayCloseService.register(dayCloseDto)).isInstanceOf(DuplicateValueException.class);

		// then
		then(dayCloseDao).should(times(1)).register(dayCloseDto);
	}

	@Test
	@DisplayName("일 마감 여부 반환")
	void successFindDayClose() {
		// givne
		DayCloseDto dayCloseDto = getDayCloseDto();
		given(dayCloseDao.existsByRestaurantIdAndCloseDate(dayCloseDto.getRestaurantId(), dayCloseDto.getCloseDate())).willReturn(true);

		// when
		dayCloseService.isRestaurantDayClose(dayCloseDto.getRestaurantId(), dayCloseDto.getCloseDate());

		// then
		then(dayCloseDao).should(times(1)).existsByRestaurantIdAndCloseDate(dayCloseDto.getRestaurantId(), dayCloseDto.getCloseDate());
	}

	@Test
	@DisplayName("일 마감 삭제 성공")
	void successDeleteDayClose() {
		// when
		dayCloseService.delete(anyLong());

		// then
		then(dayCloseDao).should(times(1)).deleteById(anyLong());
	}
}
