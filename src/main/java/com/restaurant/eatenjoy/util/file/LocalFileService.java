package com.restaurant.eatenjoy.util.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import com.restaurant.eatenjoy.exception.FileUploadFailedException;

public abstract class LocalFileService extends FileService {

	@Value("${file.root.path}")
	private String rootPath;

	@Override
	protected String transferTo(MultipartFile multipartFile, String serverFileName) throws FileUploadFailedException {
		String subPath = getSubPath();
		String uploadDirectoryPath = initializeUploadDirectoryPath(subPath);

		try {
			multipartFile.transferTo(new File(uploadDirectoryPath + "\\" + serverFileName));
		} catch (IOException e) {
			throw new FileUploadFailedException("파일 업로드에 실패하였습니다.", e);
		}

		return subPath;
	}

	private String initializeUploadDirectoryPath(String subPath) {
		String uploadDirectoryPath = Paths.get(getAbsoluteRootPath(), subPath).toString();
		File uploadDirectory = new File(uploadDirectoryPath);
		if (!uploadDirectory.exists()) {
			uploadDirectory.mkdir();
		}

		return uploadDirectoryPath;
	}

	private String getAbsoluteRootPath() {
		return Paths.get(System.getProperty("user.home"), rootPath).toString();
	}

	/**
	 * 파일을 업로드하기 위해 최상위 경로(Root 경로)의 하위 경로를 지정해야 합니다.
	 * @return 업로드될 하위 경로
	 */
	public abstract String getSubPath();

}
