package com.restaurant.eatenjoy.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.restaurant.eatenjoy.annotation.Authority;
import com.restaurant.eatenjoy.dto.MenuDto;
import com.restaurant.eatenjoy.dto.MenuInfo;
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
	public void register(@RequestPart @Valid MenuDto menuDto, @RequestPart(required = false) MultipartFile photo) {
		menuService.register(menuDto, photo);
	}

}
