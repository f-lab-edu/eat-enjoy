package com.restaurant.eatenjoy.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class SimpleMenuGroupInfo {

	private Long menuGroupId;

	private String menuGroupName;

	private Long restaurantId;

	private List<MenuInfo> menus;

	@Getter
	public static class MenuInfo {
		private Long menuId;
		private String menuName;
		private String intrDc;
	}

}
