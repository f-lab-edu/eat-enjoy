package com.restaurant.eatenjoy.dto;

import lombok.Getter;

@Getter
public class MenuInfo {

	private Long id;

	private String name;

	private String intrDc;

	private int price;

	private int sort;

	private Long menuGroupId;

	private FileDto file;

}
