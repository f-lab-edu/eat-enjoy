package com.restaurant.eatenjoy.dto.menu;

import com.restaurant.eatenjoy.dto.file.FileDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuInfo {

	private Long id;

	private String name;

	private String intrDc;

	private int price;

	private int sort;

	private Long menuGroupId;

	private FileDto file;

}
