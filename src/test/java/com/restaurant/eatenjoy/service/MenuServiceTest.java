package com.restaurant.eatenjoy.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.restaurant.eatenjoy.dao.MenuDao;
import com.restaurant.eatenjoy.dto.FileDto;
import com.restaurant.eatenjoy.dto.MenuDto;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.exception.FileNotSupportException;
import com.restaurant.eatenjoy.util.file.ImageLocalFileService;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {

	@Mock
	private MenuDao menuDao;

	@Mock
	private ImageLocalFileService fileService;

	@InjectMocks
	private MenuService menuService;

	private MenuDto menuDto;

	@BeforeEach
	void setUp() {
		menuDto = MenuDto.builder()
			.id(1L)
			.name("test")
			.intrDc("테스트 메뉴 입니다.")
			.price(1000)
			.sort(1)
			.menuGroupId(1L)
			.build();
	}

	@Test
	@DisplayName("같은 이름이 이미 존재할 경우 메뉴를 등록할 수 없다.")
	void failToRegisterMenuIfSameNameAlreadyExists() {
		doThrow(DuplicateKeyException.class).when(menuDao).register(menuDto);

		assertThatThrownBy(() -> menuService.register(menuDto, null))
			.isInstanceOf(DuplicateValueException.class);

		then(menuDao).should(times(1)).register(menuDto);
		then(menuDao).should(times(0)).updateFileIdById(any(), any());
	}

	@Test
	@DisplayName("메뉴 등록에 성공하면 데이터베이스에 성공적으로 입력된다.")
	void successToRegisterMenu() {
		menuService.register(menuDto, null);
		then(menuDao).should(times(1)).register(menuDto);
		then(menuDao).should(times(0)).updateFileIdById(any(), any());
	}

	@Test
	@DisplayName("업로드할 파일이 이미지 파일이 아니면 메뉴 등록에 실패한다.")
	void failToRegisterMenuIfUploadFileIsNotImage() {
		MultipartFile multipartFile = new MockMultipartFile("text", "test.txt", null, "text".getBytes());

		given(fileService.upload(multipartFile)).willThrow(FileNotSupportException.class);

		assertThatThrownBy(() -> menuService.register(menuDto, multipartFile))
			.isInstanceOf(FileNotSupportException.class);

		then(menuDao).should(times(1)).register(menuDto);
		then(menuDao).should(times(0)).updateFileIdById(any(), any());
	}

	@Test
	@DisplayName("업로드할 파일이 이미지 파일이면 메뉴 등록에 성공한다.")
	void successToRegisterMenuIfUploadFileIsImage() {
		MultipartFile multipartFile = new MockMultipartFile("image", "image.jpg", null, "image".getBytes());

		given(fileService.upload(multipartFile)).willReturn(FileDto.builder()
			.id(1L)
			.build());

		menuService.register(menuDto, multipartFile);

		then(menuDao).should(times(1)).register(menuDto);
		then(menuDao).should(times(1)).updateFileIdById(1L, 1L);
	}

}
