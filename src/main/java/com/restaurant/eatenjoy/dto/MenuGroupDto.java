package com.restaurant.eatenjoy.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuGroupDto {

	private Long id;

	@NotBlank(message = "메뉴 그룹명을 입력해주세요.")
	@Size(max = 20, message = "최대 20자리까지 입력 가능합니다.")
	private String name;

	@Min(1)
	private int sort;

	@Builder.Default
	private boolean used = true;

	private Long restaurantId;

}
