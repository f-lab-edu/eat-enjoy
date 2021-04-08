package com.restaurant.eatenjoy.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import com.restaurant.eatenjoy.dao.MenuGroupDao;
import com.restaurant.eatenjoy.dto.MenuGroupDto;
import com.restaurant.eatenjoy.dto.UpdateMenuGroupDto;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

	@Mock
	private MenuGroupDao menuGroupDao;

	@InjectMocks
	private MenuGroupService menuGroupService;

	private MenuGroupDto menuGroupDto;

	@BeforeEach
	void setUp() {
		menuGroupDto = MenuGroupDto.builder()
			.id(1L)
			.name("test")
			.sort(1)
			.restaurantId(1L)
			.build();
	}

	@Test
	@DisplayName("같은 이름이 이미 존재할 경우 메뉴 그룹을 등록할 수 없다.")
	void failToRegisterMenuGroupIfSameNameAlreadyExists() {
		doThrow(DuplicateKeyException.class).when(menuGroupDao).register(any());

		assertThatThrownBy(() -> menuGroupService.register(1L, menuGroupDto))
			.isInstanceOf(DuplicateValueException.class);

		then(menuGroupDao).should(times(1)).register(any());
	}

	@Test
	@DisplayName("메뉴 그룹 등록에 성공하면 데이터베이스에 성공적으로 입력된다.")
	void successToRegisterMenuGroup() {
		menuGroupService.register(1L, menuGroupDto);
		then(menuGroupDao).should(times(1)).register(any());
	}

	@Test
	@DisplayName("같은 이름이 이미 존재할 경우 메뉴 그룹명을 변경할 수 없다.")
	void failToUpdateMenuGroupNameIfSameNameAlreadyExists() {
		UpdateMenuGroupDto updateMenuGroupDto = UpdateMenuGroupDto.builder()
			.id(1L)
			.name("test")
			.sort(1)
			.build();

		doThrow(DuplicateKeyException.class).when(menuGroupDao).updateById(updateMenuGroupDto);

		assertThatThrownBy(() -> menuGroupService.update(updateMenuGroupDto))
			.isInstanceOf(DuplicateValueException.class);

		then(menuGroupDao).should(times(1)).updateById(updateMenuGroupDto);
	}

	@Test
	@DisplayName("삭제된 메뉴그룹을 수정할 경우 변경에 실패한다.")
	void failToUpdateMenuGroupIfDeletedMenuGroupChange() {
		UpdateMenuGroupDto updateMenuGroupDto = UpdateMenuGroupDto.builder()
			.id(1L)
			.name("test")
			.sort(1)
			.build();

		given(menuGroupDao.updateById(updateMenuGroupDto)).willReturn(0);

		assertThatThrownBy(() -> menuGroupService.update(updateMenuGroupDto))
			.isInstanceOf(NotFoundException.class);

		then(menuGroupDao).should(times(1)).updateById(updateMenuGroupDto);
	}

	@Test
	@DisplayName("메뉴 그룹 변경에 성공하면 데이터베이스에 성공적으로 반영된다.")
	void successToUpdateMenuGroup() {
		UpdateMenuGroupDto updateMenuGroupDto = UpdateMenuGroupDto.builder()
			.id(1L)
			.name("test")
			.sort(1)
			.build();

		given(menuGroupDao.updateById(updateMenuGroupDto)).willReturn(1);

		menuGroupService.update(updateMenuGroupDto);

		then(menuGroupDao).should(times(1)).updateById(updateMenuGroupDto);
	}

	@Test
	@DisplayName("메뉴 그룹 삭제에 성공하면 데이터베이스에 성공적으로 반영된다.")
	void successToDeleteMenuGroup() {
		menuGroupService.delete(1L);
		then(menuGroupDao).should(times(1)).deleteById(1L);
	}

}