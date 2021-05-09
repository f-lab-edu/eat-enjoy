package com.restaurant.eatenjoy.util.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.restaurant.eatenjoy.dto.FileDto;
import com.restaurant.eatenjoy.util.file.FileService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FileDeleteHandler {

	private final FileService fileService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
	public void handle(FileDto fileDto) {
		fileService.deleteFile(fileDto);
		fileService.deleteFileInfo(fileDto.getId());
	}

}
