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
import org.springframework.context.ApplicationEventPublisher;
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

	private static final Long RESTAURANT_ID = 1L;

	@Mock
	private MenuDao menuDao;

	@Mock
	private FileService fileService;

	@Mock
	private ApplicationEventPublisher eventPublisher;

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
	@DisplayName("특정 레스토랑에 같은 이름의 메뉴가 이미 존재할 경우 등록할 수 없다.")
	void failToRegisterMenuIfSameNameAlreadyExists() {
		given(menuDao.existsByRestaurantIdAndName(RESTAURANT_ID, null, menuDto.getName())).willReturn(true);

		assertThatThrownBy(() -> menuService.register(RESTAURANT_ID, menuDto))
			.isInstanceOf(DuplicateValueException.class);

		then(menuDao).should(times(1)).existsByRestaurantIdAndName(RESTAURANT_ID, null, menuDto.getName());
		then(menuDao).should(times(0)).register(menuDto);
		then(eventPublisher).should(times(0)).publishEvent(any());
	}

	@Test
	@DisplayName("업로드한 파일이 있고, 특정 레스토랑에 같은 이름의 메뉴가 이미 존재할 경우 메뉴를 등록할 수 없고 업로드된 파일은 삭제된다.")
	void failToRegisterMenuAndDeleteFileIfSameNameAndUploadFileAlreadyExists() {
		MenuDto menuDtoWithUploadFile = MenuDto.builder()
			.id(1L)
			.name("test")
			.intrDc("테스트 메뉴 입니다.")
			.price(1000)
			.sort(1)
			.menuGroupId(1L)
			.uploadFile(FileDto.builder().id(1L).build())
			.build();

		given(menuDao.existsByRestaurantIdAndName(RESTAURANT_ID, null, menuDtoWithUploadFile.getName())).willReturn(true);

		assertThatThrownBy(() -> menuService.register(RESTAURANT_ID, menuDtoWithUploadFile))
			.isInstanceOf(DuplicateValueException.class);

		then(menuDao).should(times(1)).existsByRestaurantIdAndName(RESTAURANT_ID, null, menuDto.getName());
		then(menuDao).should(times(0)).register(menuDtoWithUploadFile);
		then(eventPublisher).should(times(1)).publishEvent(menuDtoWithUploadFile.getUploadFile());
	}

	@Test
	@DisplayName("메뉴 등록에 성공하면 데이터베이스에 성공적으로 입력된다.")
	void successToRegisterMenu() {
		given(menuDao.existsByRestaurantIdAndName(RESTAURANT_ID, null, menuDto.getName())).willReturn(false);

		menuService.register(RESTAURANT_ID, menuDto);

		then(menuDao).should(times(1)).existsByRestaurantIdAndName(RESTAURANT_ID, null, menuDto.getName());
		then(menuDao).should(times(1)).register(menuDto);
	}

	@Test
	@DisplayName("업로드할 파일이 이미지 파일이 아니면 이미지 업로드에 실패한다.")
	void failToUploadImageIfUploadFileIsNotImage() {
		MultipartFile multipartFile = getMockMultipartFile("text", "test.txt");

		assertThatThrownBy(() -> menuService.uploadImage(multipartFile))
			.isInstanceOf(FileNotSupportException.class);

		then(fileService).should(times(0)).uploadFile(multipartFile);
		then(fileService).should(times(0)).saveFileInfo(any());
	}

	@Test
	@DisplayName("업로드할 파일이 이미지 파일이면 이미지 업로드에 성공한다.")
	void successToRegisterMenuIfUploadFileIsImage() {
		MultipartFile multipartFile = getMockMultipartFile("image", "image.jpg");

		FileDto fileDto = FileDto.builder()
			.id(1L)
			.build();

		given(fileService.uploadFile(multipartFile)).willReturn(fileDto);
		given(fileService.saveFileInfo(fileDto)).willReturn(1L);

		menuService.uploadImage(multipartFile);

		then(fileService).should(times(1)).uploadFile(multipartFile);
		then(fileService).should(times(1)).saveFileInfo(any());
	}

	@Test
	@DisplayName("특정 레스토랑에 같은 이름의 메뉴가 이미 존재할 경우 메뉴를 수정할 수 없다.")
	void fileToUpdateMenuIfSameNameAlreadyExists() {
		UpdateMenuDto updateMenuDto = getUpdateMenuDto();

		given(menuDao.existsByRestaurantIdAndName(RESTAURANT_ID, updateMenuDto.getId(), updateMenuDto.getName())).willReturn(true);

		assertThatThrownBy(() -> menuService.update(RESTAURANT_ID, updateMenuDto))
			.isInstanceOf(DuplicateValueException.class);

		then(menuDao).should(times(1)).existsByRestaurantIdAndName(RESTAURANT_ID, updateMenuDto.getId(), updateMenuDto.getName());
		then(menuDao).should(times(0)).updateById(updateMenuDto);
		then(eventPublisher).should(times(0)).publishEvent(any());
	}

	@Test
	@DisplayName("업로드한 파일이 있고, 특정 레스토랑에 같은 이름의 메뉴가 이미 존재할 경우 메뉴를 수정할 수 없고 업로드된 파일은 삭제된다.")
	void failToUpdateMenuAndDeleteFileIfSameNameAndUploadFileAlreadyExists() {
		UpdateMenuDto updateMenuDto = getUpdateMenuDtoWithFile();

		given(menuDao.existsByRestaurantIdAndName(RESTAURANT_ID, updateMenuDto.getId(), updateMenuDto.getName())).willReturn(true);

		assertThatThrownBy(() -> menuService.update(RESTAURANT_ID, updateMenuDto))
			.isInstanceOf(DuplicateValueException.class);

		then(menuDao).should(times(1)).existsByRestaurantIdAndName(RESTAURANT_ID, updateMenuDto.getId(), updateMenuDto.getName());
		then(menuDao).should(times(0)).updateById(updateMenuDto);
		then(eventPublisher).should(times(1)).publishEvent(updateMenuDto.getUploadFile());
	}

	@Test
	@DisplayName("메뉴 수정에 성공하면 데이터베이스에 성공적으로 입력된다.")
	void successToUpdateMenu() {
		UpdateMenuDto updateMenuDto = getUpdateMenuDto();

		given(menuDao.existsByRestaurantIdAndName(RESTAURANT_ID, updateMenuDto.getId(), updateMenuDto.getName())).willReturn(false);
		given(menuDao.findById(updateMenuDto.getId())).willReturn(getMenuInfo());

		menuService.update(RESTAURANT_ID, updateMenuDto);

		then(menuDao).should(times(1)).existsByRestaurantIdAndName(RESTAURANT_ID, updateMenuDto.getId(), updateMenuDto.getName());
		then(menuDao).should(times(1)).updateById(updateMenuDto);
		then(fileService).should(times(0)).deleteFile(any());
		then(fileService).should(times(0)).deleteFileInfo(any());
	}

	@Test
	@DisplayName("업로드 파일이 없고 기존 파일을 삭제하면 서버에서 정상적으로 파일이 삭제된다.")
	void successToDeleteSavedFileIfUploadFileIsNullAndOriginalFileIsDelete() {
		UpdateMenuDto updateMenuDto = getUpdateMenuDto();
		MenuInfo menuInfo = getMenuInfoWithFile();

		given(menuDao.existsByRestaurantIdAndName(RESTAURANT_ID, updateMenuDto.getId(), updateMenuDto.getName())).willReturn(false);
		given(menuDao.findById(updateMenuDto.getId())).willReturn(menuInfo);

		menuService.update(RESTAURANT_ID, updateMenuDto);

		then(menuDao).should(times(1)).existsByRestaurantIdAndName(RESTAURANT_ID, updateMenuDto.getId(), updateMenuDto.getName());
		then(menuDao).should(times(1)).updateById(updateMenuDto);
		then(fileService).should(times(1)).deleteFile(menuInfo.getFile());
		then(fileService).should(times(1)).deleteFileInfo(menuInfo.getFile().getId());
	}

	@Test
	@DisplayName("기존 메뉴 이미지 파일을 변경하면 서버에서 파일이 삭제된다.")
	void successToChangeSavedFileIfUploadFileIsNotNull() {
		UpdateMenuDto updateMenuDto = getUpdateMenuDtoWithFile();
		MenuInfo menuInfo = getMenuInfoWithFile();

		given(menuDao.existsByRestaurantIdAndName(RESTAURANT_ID, updateMenuDto.getId(), updateMenuDto.getName())).willReturn(false);
		given(menuDao.findById(updateMenuDto.getId())).willReturn(menuInfo);

		menuService.update(RESTAURANT_ID, updateMenuDto);

		then(menuDao).should(times(1)).existsByRestaurantIdAndName(RESTAURANT_ID, updateMenuDto.getId(), updateMenuDto.getName());
		then(menuDao).should(times(1)).updateById(updateMenuDto);
		then(fileService).should(times(1)).deleteFile(menuInfo.getFile());
		then(fileService).should(times(1)).deleteFileInfo(menuInfo.getFile().getId());
	}

	@Test
	@DisplayName("메뉴를 삭제하면 데이터베이스에 정상적으로 반영된다.")
	void successToDeleteMenu() {
		given(menuDao.findById(1L)).willReturn(getMenuInfo());

		menuService.delete(1L);

		then(menuDao).should(times(1)).deleteById(1L);
		then(fileService).should(times(0)).deleteFile(any());
		then(fileService).should(times(0)).deleteFileInfo(any());
	}

	@Test
	@DisplayName("메뉴 이미지가 존재하고 메뉴를 삭제하면 데이터베이스에 정상적으로 반영되고, 파일이 삭제된다.")
	void successToDeleteMenuAndFileIfFileExists() {
		MenuInfo menuInfo = getMenuInfoWithFile();

		given(menuDao.findById(1L)).willReturn(menuInfo);

		menuService.delete(1L);

		then(menuDao).should(times(1)).deleteById(1L);
		then(fileService).should(times(1)).deleteFile(menuInfo.getFile());
		then(fileService).should(times(1)).deleteFileInfo(menuInfo.getFile().getId());
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
			.originFile(FileDto.builder().id(1L).build())
			.uploadFile(FileDto.builder().id(2L).build())
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
