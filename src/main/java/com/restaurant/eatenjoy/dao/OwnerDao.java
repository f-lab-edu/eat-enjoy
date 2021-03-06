package com.restaurant.eatenjoy.dao;

import org.apache.ibatis.annotations.Param;

import com.restaurant.eatenjoy.dto.owner.OwnerDto;

public interface OwnerDao {

	void register(OwnerDto ownerDto);

	boolean existsByLoginId(String loginId);

	boolean existsByEmail(String email);

	Long findIdByLoginIdAndPassword(@Param("loginId") String loginId, @Param("password") String password);

	boolean existsByIdAndPassword(@Param("id") Long id, @Param("password") String password);

	void updateEmailCertified(String email);

	OwnerDto findById(Long id);

	void deleteById(Long id);

	void updatePassword(@Param("id") Long id, @Param("password") String password);

	void updateMailById(OwnerDto ownerDto);

}
