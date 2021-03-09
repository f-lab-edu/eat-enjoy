package com.restaurant.eatenjoy.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.print.attribute.standard.Media;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.eatenjoy.controller.UserController;
import com.restaurant.eatenjoy.dto.LoginDto;
import com.restaurant.eatenjoy.dto.UserDto;

@SpringBootTest
@AutoConfigureMockMvc
class LoginServiceTest {

	private final String USERSESSION = "USERSSESSION";

	@Autowired
	MockMvc mockMvc;

	@Autowired
	HttpSession httpSession;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	LoginService loginService;

	@Autowired
	UserController userController;

	UserDto userDto;

	LoginDto loginDto;

	@BeforeEach
	void userCreate() {
		userDto = UserDto.builder()
			.loginId("test")
			.password("test123")
			.email("test@gmail.com")
			.regionCD("cod")
			.build();

		loginDto = LoginDto.builder()
			.loginId(userDto.getLoginId())
			.password(userDto.getPassword())
			.build();
	}

	@Test
	@DisplayName("로그인 성공")
	public void loginSuccess() throws Exception {
		String json = objectMapper.writeValueAsString(loginDto);

		this.mockMvc.perform(post("/api/users/login")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("아이디 혹은 비밀번호 잘못 입력 - 로그인 실패: 404 NOT_FOUND")
	public void loginFail() throws Exception {

		loginDto = LoginDto.builder()
			.loginId(userDto.getLoginId())
			.password("test1234")
			.build();

		String json = objectMapper.writeValueAsString(loginDto);

		this.mockMvc.perform(post("/api/users/login")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("로그아웃 실패: 404 not found")
	public void logoutFail() throws Exception {

		this.mockMvc.perform(post("/api/users/logout"))
			.andDo(print())
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("로그인 성공: 200 ok")
	public void logoutSuccess() {
		userController.loginUser(loginDto);
		ResponseEntity<HttpStatus> httpStatusResponseEntity = userController.logoutUser();

		System.out.println(httpStatusResponseEntity.getStatusCode());
	}
}
