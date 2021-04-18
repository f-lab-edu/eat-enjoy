package com.restaurant.eatenjoy.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.eatenjoy.annotation.Authority;
import com.restaurant.eatenjoy.dto.MenuGroupDto;
import com.restaurant.eatenjoy.dto.UpdateMenuGroupDto;
import com.restaurant.eatenjoy.service.MenuGroupService;
import com.restaurant.eatenjoy.util.cache.CacheNames;
import com.restaurant.eatenjoy.util.security.Role;

import lombok.RequiredArgsConstructor;

@Authority(Role.OWNER)
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/menu-groups")
@RequiredArgsConstructor
public class MenuGroupController {

	private final MenuGroupService menuGroupService;

	@GetMapping
	@Cacheable(value = CacheNames.MENU_GROUP, key = "#restaurantId")
	public List<MenuGroupDto> menuGroups(@PathVariable Long restaurantId) {
		return menuGroupService.findAllByRestaurantId(restaurantId);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@CacheEvict(value = CacheNames.MENU_GROUP, key = "#restaurantId")
	public void register(@PathVariable Long restaurantId, @RequestBody @Valid MenuGroupDto menuGroupDto) {
		menuGroupService.register(restaurantId, menuGroupDto);
	}

	@PutMapping
	@CacheEvict(value = CacheNames.MENU_GROUP, key = "#restaurantId")
	public void update(@PathVariable Long restaurantId, @RequestBody @Valid UpdateMenuGroupDto menuGroupDto) {
		menuGroupService.update(menuGroupDto);
	}

	@DeleteMapping("/{menuGroupId}")
	@CacheEvict(value = CacheNames.MENU_GROUP, key = "#restaurantId")
	public void delete(@PathVariable Long restaurantId, @PathVariable Long menuGroupId) {
		menuGroupService.delete(menuGroupId);
	}

}
