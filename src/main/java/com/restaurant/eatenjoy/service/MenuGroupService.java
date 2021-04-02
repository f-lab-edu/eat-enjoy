package com.restaurant.eatenjoy.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.eatenjoy.dao.MenuGroupDao;
import com.restaurant.eatenjoy.dto.MenuGroupDto;
import com.restaurant.eatenjoy.exception.DuplicateValueException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuGroupService {

	private final MenuGroupDao menuGroupDao;

	@Transactional
	public void register(Long restaurantId, MenuGroupDto menuGroupDto) {
		validateName(menuGroupDto.getName());

		menuGroupDao.register(MenuGroupDto.builder()
			.name(menuGroupDto.getName())
			.sort(menuGroupDto.getSort())
			.used(menuGroupDto.isUsed())
			.restaurantId(restaurantId)
			.build());
	}

	private void validateName(String name) {
		if (menuGroupDao.existsByName(name)) {
			throw new DuplicateValueException(name + "은(는) 이미 존재하는 메뉴그룹명 입니다.");
		}
	}

}
