package com.restaurant.eatenjoy.dao.impl;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.restaurant.eatenjoy.dao.UserDao;
import com.restaurant.eatenjoy.dto.UserDto;

@Repository
public class UserDaoImpl implements UserDao {

	@Autowired
	SqlSession sqlSession;

	@Override
	public void insertUser(UserDto userDto) {
		sqlSession.insert("insertUser", userDto);
	}

	@Override
	public boolean readUserLoginId(String loginId) {
		return sqlSession.selectOne("readUserLoginId", loginId);
	}

	@Override
	public boolean readUserEmail(String email) {
		return sqlSession.selectOne("readUserEmail", email);
	}
}
