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
import com.restaurant.eatenjoy.dto.MenuInfo;
import com.restaurant.eatenjoy.dto.UpdateMenuDto;
import com.restaurant.eatenjoy.exception.DuplicateValueException;
import com.restaurant.eatenjoy.exception.FileNotSupportException;
import com.restaurant.eatenjoy.util.file.FileService;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {

	@Mock
	private MenuDao menuDao;

	@Mock
	private FileService fileService;

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
		then(fileService).should(times(0)).uploadFile(null);
		then(fileService).should(times(0)).saveFileInfo(any());
		then(menuDao).should(times(0)).updateFileIdById(any(), any());
	}

	@Test
	@DisplayName("메뉴 등록에 성공하면 데이터베이스에 성공적으로 입력된다.")
	void successToRegisterMenu() {
		menuService.register(menuDto, null);
		then(menuDao).should(times(1)).register(menuDto);
		then(fileService).should(times(0)).uploadFile(null);
		then(fileService).should(times(0)).saveFileInfo(any());
		then(menuDao).should(times(0)).updateFileIdById(any(), any());
	}

	@Test
	@DisplayName("업로드할 파일이 이미지 파일이 아니면 메뉴 등록에 실패한다.")
	void failToRegisterMenuIfUploadFileIsNotImage() {
		MultipartFile multipartFile = getMockMultipartFile("text", "test.txt");

		assertThatThrownBy(() -> menuService.register(menuDto, multipartFile))
			.isInstanceOf(FileNotSupportException.class);

		then(menuDao).should(times(1)).register(menuDto);
		then(fileService).should(times(0)).uploadFile(multipartFile);
		then(fileService).should(times(0)).saveFileInfo(any());
		then(menuDao).should(times(0)).updateFileIdById(any(), any());
	}

	@Test
	@DisplayName("업로드할 파일이 이미지 파일이면 메뉴 등록에 성공한다.")
	void successToRegisterMenuIfUploadFileIsImage() {
		MultipartFile multipartFile = getMockMultipartFile("image", "image.jpg");

		FileDto fileDto = FileDto.builder()
			.id(1L)
			.build();

		given(fileService.uploadFile(multipartFile)).willReturn(fileDto);
		given(fileService.saveFileInfo(fileDto)).willReturn(1L);

		menuService.register(menuDto, multipartFile);

		then(menuDao).should(times(1)).register(menuDto);
		then(fileService).should(times(1)).uploadFile(multipartFile);
		then(fileService).should(times(1)).saveFileInfo(any());
		then(menuDao).should(times(1)).updateFileIdById(1L, 1L);
	}

	@Test
	@DisplayName("메뉴 수정에 성공하면 데이터베이스에 성공적으로 입력된다.")
	void successToUpdateMenu() {
		UpdateMenuDto updateMenuDto = getUpdateMenuDto();

		given(menuDao.findById(updateMenuDto.getId())).willReturn(getMenuInfo());

		menuService.update(updateMenuDto, null);

		then(menuDao).should(times(1)).updateById(updateMenuDto);
		then(fileService).should(times(0)).deleteFile(any());
		then(fileService).should(times(0)).deleteFileInfo(any());
		then(fileService).should(times(0)).uploadFile(any());
		then(fileService).should(times(0)).saveFileInfo(any());
	}

	@Test
	@DisplayName("기존 파일이 존재하지 않고 파일 업로드 시 성공적으로 업로드 된다.")
	void successToUploadMenuImageIfOriginalFileNotExists() {
		MultipartFile multipartFile = getMockMultipartFile("image", "image.jpg");
		UpdateMenuDto updateMenuDto = getUpdateMenuDto();

		given(menuDao.findById(updateMenuDto.getId())).willReturn(getMenuInfo());

		menuService.update(updateMenuDto, multipartFile);

		then(menuDao).should(times(1)).updateById(updateMenuDto);
		then(fileService).should(times(0)).deleteFile(any());
		then(fileService).should(times(0)).deleteFileInfo(any());
		then(fileService).should(times(1)).uploadFile(multipartFile);
		then(fileService).should(times(1)).saveFileInfo(any());
	}

	@Test
	@DisplayName("업로드 파일이 없고 기존 파일을 삭제하면 서버에서 정상적으로 파일이 삭제된다.")
	void successToDeleteSavedFileIfUploadFileIsNullAndOriginalFileIsDelete() {
		UpdateMenuDto updateMenuDto = getUpdateMenuDto();
		MenuInfo menuInfo = getMenuInfoWithFile();

		given(menuDao.findById(updateMenuDto.getId())).willReturn(menuInfo);

		menuService.update(updateMenuDto, null);

		then(menuDao).should(times(1)).updateById(updateMenuDto);
		then(fileService).should(times(1)).deleteFile(menuInfo.getFile());
		then(fileService).should(times(1)).deleteFileInfo(menuInfo.getFile().getId());
		then(fileService).should(times(0)).uploadFile(any());
		then(fileService).should(times(0)).saveFileInfo(any());
	}

	@Test
	@DisplayName("기존 메뉴 이미지 파일을 변경하면 서버에서 파일이 삭제 후 업로드 된다.")
	void successToChangeSavedFileIfUploadFileIsNotNull() {
		MultipartFile multipartFile = getMockMultipartFile("image", "image.jpg");
		UpdateMenuDto updateMenuDto = getUpdateMenuDtoWithFile();
		MenuInfo menuInfo = getMenuInfoWithFile();

		given(menuDao.findById(updateMenuDto.getId())).willReturn(menuInfo);

		menuService.update(updateMenuDto, multipartFile);

		then(menuDao).should(times(1)).updateById(updateMenuDto);
		then(fileService).should(times(1)).deleteFile(menuInfo.getFile());
		then(fileService).should(times(1)).deleteFileInfo(menuInfo.getFile().getId());
		then(fileService).should(times(1)).uploadFile(multipartFile);
		then(fileService).should(times(1)).saveFileInfo(any());
	}

	private MockMultipartFile getMockMultipartFile(String name, String originalFilename) {
		return new MockMultipartFile(name, originalFilename, null, name.getBytes());
	}

	private UpdateMenuDto getUpdateMenuDto() {
		return UpdateMenuDto.builder()
			.id(1L)
			.name("test")
			.intrDc("테스트 메뉴 입니다.")
			.price(1000)
			.sort(1)
			.menuGroupId(1L)
			.build();
	}

	private UpdateMenuDto getUpdateMenuDtoWithFile() {
		return UpdateMenuDto.builder()
			.id(1L)
			.name("test")
			.intrDc("테스트 메뉴 입니다.")
			.price(1000)
			.sort(1)
			.menuGroupId(1L)
			.file(FileDto.builder().id(1L).build())
			.build();
	}

	private MenuInfo getMenuInfo() {
		return MenuInfo.builder()
			.id(1L)
			.build();
	}

	private MenuInfo getMenuInfoWithFile() {
		return MenuInfo.builder()
			.id(1L)
			.file(FileDto.builder().id(1L).build())
			.build();
	}

}
