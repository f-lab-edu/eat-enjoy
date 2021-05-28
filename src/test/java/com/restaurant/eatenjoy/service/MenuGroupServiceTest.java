package com.restaurant.eatenjoy.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;

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
	@DisplayName("메뉴 그룹 변경에 성공하면 데이터베이스에 성공적으로 반영된다.")
	void successToUpdateMenuGroup() {
		UpdateMenuGroupDto updateMenuGroupDto = UpdateMenuGroupDto.builder()
			.id(1L)
			.name("test")
			.sort(1)
			.build();

		menuGroupService.update(updateMenuGroupDto);

		then(menuGroupDao).should(times(1)).updateById(updateMenuGroupDto);
	}

	@Test
	@DisplayName("메뉴가 존재하면 메뉴 그룹을 삭제할 수 없다.")
	void failToDeleteMenuGroupIfMenuExists() {
		given(menuGroupDao.existsMenusById(1L)).willReturn(true);

		assertThatThrownBy(() -> menuGroupService.delete(1L))
			.isInstanceOf(IllegalArgumentException.class);

		then(menuGroupDao).should(times(1)).existsMenusById(1L);
		then(menuGroupDao).should(times(0)).deleteById(1L);
	}

	@Test
	@DisplayName("메뉴 그룹 삭제에 성공하면 데이터베이스에 성공적으로 반영된다.")
	void successToDeleteMenuGroup() {
		given(menuGroupDao.existsMenusById(1L)).willReturn(false);

		menuGroupService.delete(1L);

		then(menuGroupDao).should(times(1)).existsMenusById(1L);
		then(menuGroupDao).should(times(1)).deleteById(1L);
	}

	@Test
	@DisplayName("레스토랑 아이디에 속한 메뉴그룹들을 조회한다.")
	void findAllByRestaurantId() {
		List<MenuGroupDto> menuGroups = new ArrayList<>();
		for (int i = 1; i < 5; i++) {
			menuGroups.add(MenuGroupDto.builder()
				.id((long) i)
				.name("menuGroup" + i)
				.sort(i)
				.restaurantId(1L)
				.build());
		}

		given(menuGroupDao.findAllByRestaurantId(1L)).willReturn(menuGroups);

		assertThat(menuGroupService.findAllByRestaurantId(1L)).isEqualTo(menuGroups);

		then(menuGroupDao).should(times(1)).findAllByRestaurantId(1L);
	}

	@Test
	@DisplayName("메뉴그룹이 존재하지 않을 경우 빈 리스트를 반환한다.")
	void getEmptyListIfMenuGroupNotExists() {
		given(menuGroupDao.findAllByRestaurantId(1L)).willReturn(new ArrayList<>());

		assertThat(menuGroupService.findAllByRestaurantId(1L)).hasSize(0);

		then(menuGroupDao).should(times(1)).findAllByRestaurantId(1L);
	}

}