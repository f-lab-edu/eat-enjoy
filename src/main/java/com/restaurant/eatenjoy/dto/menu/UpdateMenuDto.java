package com.restaurant.eatenjoy.dto.menu;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.restaurant.eatenjoy.dto.file.FileDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMenuDto {

	@NotNull(message = "메뉴 아이디가 존재하지 않습니다.")
	private Long id;

	@NotBlank(message = "메뉴명을 입력해주세요.")
	@Size(max = 20, message = "최대 20자리까지 입력 가능합니다.")
	private String name;

	@NotBlank(message = "메뉴 소개글을 입력해주세요.")
	private String intrDc;

	@Min(1)
	private int price;

	@Min(1)
	private int sort;

	@NotNull(message = "메뉴그룹을 선택하세요.")
	private Long menuGroupId;

	private FileDto originFile;

	private FileDto uploadFile;

}
