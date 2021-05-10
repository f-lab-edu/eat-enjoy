package com.restaurant.eatenjoy.controller;

import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.restaurant.eatenjoy.annotation.Authority;
import com.restaurant.eatenjoy.annotation.LoginAuthId;
import com.restaurant.eatenjoy.annotation.OwnersRestaurantCheck;
import com.restaurant.eatenjoy.dto.FileDto;
import com.restaurant.eatenjoy.dto.RestaurantDto;
import com.restaurant.eatenjoy.dto.RestaurantInfo;
import com.restaurant.eatenjoy.dto.RestaurantListDto;
import com.restaurant.eatenjoy.dto.UpdateRestaurant;
import com.restaurant.eatenjoy.exception.NotFoundException;
import com.restaurant.eatenjoy.service.RestaurantService;
import com.restaurant.eatenjoy.util.security.Role;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
@Authority(Role.OWNER)
public class RestaurantController {

	private final RestaurantService restaurantService;

	/*
	 * 사장님의 식당을 등록한다
	 * @param restaurantDto 식당 정보
	 * @param ownerId 로그인한 사장님의 id
	 * */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void addRestaurant(@RequestBody @Valid RestaurantDto restaurantDto, @LoginAuthId Long ownerId) {
		restaurantService.register(restaurantDto, ownerId);
	}

	/*
	 * 사장님의 식당 리스트 정보를 조회한다
	 * @param lastRestaurantId
	 * @param ownerId 로그인한 사장님의 id
	 * @return List<RestaurantListDto>
	 * */
	@GetMapping
	public List<RestaurantListDto> getRestaurantList(Long lastRestaurantId, @LoginAuthId Long ownerId) {

		if (Objects.isNull(lastRestaurantId)) {
			throw new NotFoundException("식당 조회에 실패하였습니다");
		}

		return restaurantService.getListOfRestaurant(lastRestaurantId, ownerId);
	}

	/*
	 * 사장님의 식당 정보를 조회한다
	 * @param restaurantId 조회할 레스토랑의 id
	 * @return RestaurantInfo
	 * */
	@GetMapping("{restaurantId}")
	@OwnersRestaurantCheck
	public RestaurantInfo getRestaurant(@PathVariable Long restaurantId) {
		return restaurantService.findById(restaurantId);
	}

	/*
	 * 사장님의 식당 정보를 수정한다
	 * @param restaurantId 수정할 레스토랑의 id
	 * */
	@PutMapping("{restaurantId}")
	@OwnersRestaurantCheck
	public void updateRestaurant(@RequestBody @Valid UpdateRestaurant restaurant) {
		restaurantService.updateRestaurant(restaurant);
	}

	/*
	 * 식당 이미지를 업로드 한다
	 * @param photo 업로드할 이미지
	 * */
	@PostMapping("/images")
	@ResponseStatus(HttpStatus.CREATED)
	public FileDto imageUpload(@RequestPart MultipartFile photo) {
		return restaurantService.uploadImage(photo);
	}
}
