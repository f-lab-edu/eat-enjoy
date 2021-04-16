package com.restaurant.eatenjoy.service;

import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.eatenjoy.dao.MenuGroupDao;
import com.restaurant.eatenjoy.dto.MenuGroupDto;
import com.restaurant.eatenjoy.dto.UpdateMenuGroupDto;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuGroupService {

	private final MenuGroupDao menuGroupDao;

	public List<MenuGroupDto> findAllByRestaurantId(Long restaurantId) {
		return menuGroupDao.findAllByRestaurantId(restaurantId);
	}

	@Transactional
	public void register(Long restaurantId, MenuGroupDto menuGroupDto) {
		try {
			menuGroupDao.register(MenuGroupDto.builder()
				.name(menuGroupDto.getName())
				.sort(menuGroupDto.getSort())
				.restaurantId(restaurantId)
				.build());
		} catch (DuplicateKeyException ex) {
			throw new DuplicateValueException(menuGroupDto.getName() + "은(는) 이미 존재하는 메뉴그룹명 입니다.", ex);
		}
	}

	@Transactional
	public void update(UpdateMenuGroupDto menuGroupDto) {
		try {
			int result = menuGroupDao.updateById(menuGroupDto);
			if (result == 0) {
				throw new NotFoundException("해당 메뉴그룹을 찾을 수 없습니다.");
			}
		} catch (DuplicateKeyException ex) {
			throw new DuplicateValueException(menuGroupDto.getName() + "은(는) 이미 존재하는 메뉴그룹명 입니다.", ex);
		}
	}

	@Transactional
	public void delete(Long menuGroupId) {
		menuGroupDao.deleteById(menuGroupId);
	}

}
