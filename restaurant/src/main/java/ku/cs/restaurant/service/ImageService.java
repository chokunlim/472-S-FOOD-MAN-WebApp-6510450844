package ku.cs.restaurant.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class ImageService {
    public String saveImage(String folderPath, MultipartFile image) throws IOException {
        Files.createDirectories(Paths.get(folderPath));
        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        validateFileName(fileName);
        Path path = Paths.get(folderPath, fileName).normalize();

        if (!path.startsWith(Paths.get(folderPath).normalize())) {
            throw new IllegalArgumentException("Invalid file path");
        }

        Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        return path.toString();
    }

    private void validateFileName(String fileName) {
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            throw new IllegalArgumentException("Invalid filename");
        }
    }
}
