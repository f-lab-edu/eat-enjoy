package com.restaurant.eatenjoy.dao;

import com.restaurant.eatenjoy.dto.OwnerDto;

public interface OwnerDao {

	void register(OwnerDto ownerDto);

	boolean existsByLoginId(String loginId);

	boolean existsByEmail(String email);

}
