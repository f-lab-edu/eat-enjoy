package com.restaurant.eatenjoy.dto.restaurant;

import com.restaurant.eatenjoy.dto.file.FileDto;

import lombok.Getter;

@Getter
public class SimpleRestaurantDto {

	private Long id;

	private String name;

	private String intrDc;

	private String bname;

	private FileDto file;

}
