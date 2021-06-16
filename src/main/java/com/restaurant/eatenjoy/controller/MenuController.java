package com.restaurant.eatenjoy.controller;

import javax.validation.Valid;

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
import com.restaurant.eatenjoy.dto.menu.MenuDto;
import com.restaurant.eatenjoy.dto.menu.MenuInfo;
import com.restaurant.eatenjoy.dto.menu.UpdateMenuDto;
import com.restaurant.eatenjoy.service.MenuService;
import com.restaurant.eatenjoy.util.security.Role;

import lombok.RequiredArgsConstructor;

@Authority(Role.OWNER)
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/menus")
@RequiredArgsConstructor
public class MenuController {

	private final MenuService menuService;

	@GetMapping("/{menuId}")
	public MenuInfo menuInfo(@PathVariable Long menuId) {
		return menuService.getMenuInfo(menuId);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void register(@PathVariable Long restaurantId, @RequestBody @Valid MenuDto menuDto) {
		menuService.register(restaurantId, menuDto);
	}

	@PutMapping
	public void update(@PathVariable Long restaurantId, @RequestBody @Valid UpdateMenuDto updateMenuDto) {
		menuService.update(restaurantId, updateMenuDto);
	}

	@DeleteMapping("/{menuId}")
	public void delete(@PathVariable Long menuId) {
		menuService.delete(menuId);
	}

}
