package com.restaurant.eatenjoy.util.file;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.restaurant.eatenjoy.dao.FileDao;
import com.restaurant.eatenjoy.dto.FileDto;
import com.restaurant.eatenjoy.exception.FileUploadFailedException;

import lombok.RequiredArgsConstructor;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class AwsS3FileService implements FileService {

	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

	private final AmazonS3 amazonS3;

	private final FileDao fileDao;

	@Value("${cloud.s3.bucket.name}")
	private String bucketName;

	@Override
	public FileDto uploadFile(MultipartFile multipartFile) {
		String serverFileName = UUID.randomUUID().toString();
		String objectKey = LocalDate.now().format(dateFormatter) + "/" +  serverFileName;

		putFileInBucket(multipartFile, objectKey);

		return FileDto.builder()
			.origFilename(multipartFile.getOriginalFilename())
			.serverFilename(serverFileName)
			.filePath(amazonS3.getUrl(bucketName, objectKey).toString())
			.size(multipartFile.getSize())
			.build();
	}

	@Override
	public Long saveFileInfo(FileDto fileDto) {
		fileDao.register(fileDto);

		return fileDto.getId();
	}

	@Override
	public void deleteFile(FileDto fileDto) {
		amazonS3.deleteObject(bucketName, getObjectKeyByFilePath(fileDto.getFilePath()));
	}

	@Override
	public void deleteFileInfo(Long fileId) {
		fileDao.deleteById(fileId);
	}

	@Override
	public void deleteFiles(List<FileDto> fileDtos) {
		amazonS3.deleteObjects(createDeleteObjectsRequest(fileDtos));
	}

	@Override
	public void deleteFileInfos(List<FileDto> fileDtos) {
		fileDao.deleteByIdIn(fileDtos);
	}

	private void putFileInBucket(MultipartFile multipartFile, String objectKey) {
		try {
			amazonS3.putObject(new PutObjectRequest(bucketName, objectKey, multipartFile.getInputStream(), createObjectMetadata(multipartFile))
				.withCannedAcl(CannedAccessControlList.PublicRead));
		} catch (IOException e) {
			throw new FileUploadFailedException("파일 업로드에 실패하였습니다.", e);
		}
	}

	private ObjectMetadata createObjectMetadata(MultipartFile file) {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(file.getSize());
		metadata.setContentType(file.getContentType());

		return metadata;
	}

	private DeleteObjectsRequest createDeleteObjectsRequest(List<FileDto> fileDtos) {
		DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
		deleteObjectsRequest.setKeys(fileDtos.stream()
			.map(fileDto -> new DeleteObjectsRequest.KeyVersion(getObjectKeyByFilePath(fileDto.getFilePath())))
			.collect(Collectors.toList()));

		return deleteObjectsRequest;
	}

	private String getObjectKeyByFilePath(String filePath) {
		try {
			URL url = new URL(filePath);

			return url.getPath().substring(1);
		} catch (MalformedURLException e) {
			throw new FileUploadFailedException("파일 업로드에 실패하였습니다.", e);
		}
	}

}
