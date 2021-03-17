package com.restaurant.eatenjoy.dao;

import org.apache.ibatis.annotations.Param;

import com.restaurant.eatenjoy.dto.OwnerDto;

public interface OwnerDao {

	void register(OwnerDto ownerDto);

	boolean existsByLoginId(String loginId);

	boolean existsByEmail(String email);

	boolean existsByLoginIdAndPassword(@Param("loginId") String loginId, @Param("password") String password);

	void updateEmailCertified(String email);

	OwnerDto findByLoginId(String loginId);

}
