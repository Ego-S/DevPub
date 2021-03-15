package com.devpub.application.service;

import com.devpub.application.dto.exception.BadRequestException;
import com.devpub.application.dto.response.ResultDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class UploadService {

	@Value("${uploadDir}")
	private String uploadDir;
	@Value("${maxFileSizeInBytes}")
	private int maxFileSize;

	public ResponseEntity<?> uploadImage(MultipartFile file) {
		Map<String, String> errors = new HashMap<>();
		if (file.getSize() > maxFileSize) {
			long mb = maxFileSize / (1024 * 1024);
			errors.put("image", "The max file size for upload is " + mb + " Mb");
		}

		if (file.getContentType() != null && !file.getContentType().startsWith("image")) {
			errors.put("type", "The file type is not image");
		}

		if (errors.isEmpty()) {
			return ResponseEntity.ok(saveImage(file));
		} else {
			return ResponseEntity.badRequest().body(new ResultDTO(false, errors));
		}
	}

	private String saveImage(MultipartFile file) {
		Path path = getUploadPath(file);
		String folder = path.toString().substring(0, path.toString().lastIndexOf(File.separator));
		try {
			File dir = new File(folder);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File newFile = new File(path.toString());

			newFile.createNewFile();

			FileOutputStream outputStream = new FileOutputStream(newFile);
			outputStream.write(file.getBytes());
			outputStream.close();

		} catch (IOException e) {
			e.printStackTrace();
			throw new BadRequestException();
		}
		return path.toString();
	}

	private Path getUploadPath(MultipartFile file) {
		int hashCode = file.hashCode();
		StringBuilder stringBuilder = new StringBuilder(String.valueOf(hashCode));
		int offset = stringBuilder.length() / 3;
		for (int i = 0; i < 3; i++) {
			stringBuilder.insert(i * (offset + 1), File.separator);
		}
		stringBuilder
				.append(File.separator)
				.append(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));
		String path = uploadDir + stringBuilder.toString();

		//if file with this path already exists we add additional symbol in file name
		while (true) {
			if (new File(path).exists()) {
				int lastSeparatorIndex = path.lastIndexOf(File.separator);
				path = path.substring(0, lastSeparatorIndex + 1) + "0" + path.substring(lastSeparatorIndex + 1);
			} else {
				break;
			}
		}

		return Paths.get(path);
	}
}
