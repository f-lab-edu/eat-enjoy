package com.restaurant.eatenjoy.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.eatenjoy.dao.MenuGroupDao;
import com.restaurant.eatenjoy.dto.MenuGroupDto;
import com.restaurant.eatenjoy.dto.UpdateMenuGroupDto;
import com.restaurant.eatenjoy.exception.DuplicateValueException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuGroupService {

	private final MenuGroupDao menuGroupDao;

	@Transactional
	public void register(Long restaurantId, MenuGroupDto menuGroupDto) {
		try {
			menuGroupDao.register(MenuGroupDto.builder()
				.name(menuGroupDto.getName())
				.sort(menuGroupDto.getSort())
				.used(menuGroupDto.isUsed())
				.restaurantId(restaurantId)
				.build());
		} catch (DuplicateKeyException ex) {
			throw new DuplicateValueException(menuGroupDto.getName() + "은(는) 이미 존재하는 메뉴그룹명 입니다.", ex);
		}
	}

	@Transactional
	public void update(UpdateMenuGroupDto menuGroupDto) {
		try {
			menuGroupDao.updateById(menuGroupDto);
		} catch (DuplicateKeyException ex) {
			throw new DuplicateValueException(menuGroupDto.getName() + "은(는) 이미 존재하는 메뉴그룹명 입니다.", ex);
		}
	}

}
