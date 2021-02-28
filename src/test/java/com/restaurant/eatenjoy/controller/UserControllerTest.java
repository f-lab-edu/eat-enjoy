package com.restaurant.eatenjoy.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.eatenjoy.dto.UserDto;
import com.restaurant.eatenjoy.service.UserService;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	UserService userService;

	@BeforeEach
	UserDto userCreate() {
		UserDto userDto = UserDto.builder()
			.loginId("test")
			.password("test123")
			.email("test@gmail.com")
			.regionCD("cod")
			.build();

		return userDto;
	}

	@Test
	@DisplayName("중복된 아이디가 존재한다면 회원 가입 할 수 없다")
	public void duplicatedId() throws Exception {
		assertThat(userService.isDuplicatedId(userCreate().getLoginId())).isEqualTo(false);
	}

	@Test
	@DisplayName("중복된 이메일이 존재한다면 회원 가입 할 수 없다")
	public void duplicatedEmail() throws Exception {
		assertThat(userService.isDuplicatedEmail(userCreate().getEmail())).isEqualTo(false);
	}

	@Test
	@DisplayName("중복된 아이디와 이메일이 없다면 회원가입 성공")
	public void createUsers() throws Exception {
		String json = objectMapper.writeValueAsString(userCreate());

		this.mockMvc.perform(post("/api/users")
			.content(json)
			.contentType(MediaType.APPLICATION_JSON)
		)
			.andDo(print())
			.andExpect(status().isCreated());
	}
}
