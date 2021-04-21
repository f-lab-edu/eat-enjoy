package com.restaurant.eatenjoy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {

	private Long id;

	private String origFilename;

	private String serverFilename;

	private String filePath;

	private Long size;

}
