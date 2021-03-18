package com.devpub.application.service;

import com.devpub.application.dto.exception.BadRequestException;
import com.devpub.application.dto.response.ResultDTO;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class UploadService {

	@Value("${uploadDir}")
	private String uploadDir;
	@Value("${maxFileSizeInBytes}")
	private int maxFileSize;
	@Value("${avatarHeight}")
	private int avatarHeight;
	@Value("${avatarWidth}")
	private int avatarWidth;

	public ResponseEntity<?> uploadImage(MultipartFile file) {
		Map<String, String> errors = checkErrors(file);

		if (errors.isEmpty()) {
			return ResponseEntity.ok(saveImage(file));
		} else {
			return ResponseEntity.badRequest().body(new ResultDTO(false, errors));
		}
	}

	public String saveImage(MultipartFile file) {
		Path path = getUploadPath(file);
		try {
			BufferedImage image = ImageIO.read(file.getInputStream());
			return saveBufferedImage(path, image);
		} catch (IOException e) {
			e.printStackTrace();
			throw new BadRequestException();
		}
	}

	public String saveAvatar(MultipartFile avatar) {
		Path path = getUploadPath(avatar);
		try {
			BufferedImage image = ImageIO.read(avatar.getInputStream());

			int width = image.getWidth();
			int height = image.getHeight();
			double proportion = (double) height / width;

			//resize with proportions. Smallest side must be equals avatar size
			if (width < height) {
				width = avatarWidth;
				height = (int) (avatarWidth * proportion);
			} else {
				height = avatarHeight;
				width = (int) (avatarHeight / proportion);
			}

			BufferedImage resizedImage = Scalr.resize(
					image, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_EXACT, width, height);

			int x = (width - avatarWidth) / 2;
			int y = (height - avatarHeight) / 2;

			BufferedImage finalSize = resizedImage.getSubimage(x, y, avatarWidth, avatarHeight);

			return saveBufferedImage(path, finalSize);
		} catch (IOException e) {
			e.printStackTrace();
			throw new BadRequestException();
		}
	}

	public String saveBufferedImage(Path path, BufferedImage image) throws IOException {
		String folder = path.toString().substring(0, path.toString().lastIndexOf(File.separator));
		String formatName = getFormatName(path);

		File dir = new File(folder);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File newFile = new File(path.toString());
		ImageIO.write(image, formatName, newFile);
		image.flush();

		return path.toString();
	}

	public Map<String, String> checkErrors(MultipartFile file) {
		Map<String, String> errors = new HashMap<>();
		if (file.getSize() > maxFileSize) {
			long mb = maxFileSize / (1024 * 1024);
			errors.put("image", "The max file size for upload is " + mb + " Mb");
		}

		if (file.getContentType() != null && !file.getContentType().startsWith("image")) {
			errors.put("type", "The file type is not image");
		}
		return errors;
	}

	public void deleteFile(String path) {
		if (path != null && !path.equals("") && path.contains(".") && path.contains(File.separator)) {
			String thirdFolderPath = path.substring(0, path.lastIndexOf(File.separator));
			String secondFolderPath = thirdFolderPath.substring(0, thirdFolderPath.lastIndexOf(File.separator));
			String firstFolderPath = secondFolderPath.substring(0, secondFolderPath.lastIndexOf(File.separator));

			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}

			File thirdFolder = new File(thirdFolderPath);
			if (Objects.requireNonNull(thirdFolder.list()).length == 0) {
				thirdFolder.delete();
			}

			File secondFolder = new File(secondFolderPath);
			if (Objects.requireNonNull(secondFolder.list()).length == 0) {
				secondFolder.delete();
			}

			File firstFolder = new File(firstFolderPath);
			if (Objects.requireNonNull(firstFolder.list()).length == 0) {
				firstFolder.delete();
			}
		}
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


	private String getFormatName(Path path) {
		String format = "";
		String pathStr = path.toString();
		if (pathStr.contains(".")) {
			format = pathStr.substring(pathStr.lastIndexOf(".") + 1);
		}
		if (format.equals("jpeg") || format.equals("jpg") || format.equals("png")) {
			return format;
		} else {
			System.out.println(format + " - wrong file format");
			throw new BadRequestException();
		}
	}
}
