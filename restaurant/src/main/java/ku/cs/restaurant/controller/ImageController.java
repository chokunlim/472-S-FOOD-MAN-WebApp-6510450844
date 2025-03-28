package ku.cs.restaurant.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageController {
    private final ResourceLoader resourceLoader;

    public ImageController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @GetMapping("/images/foods/{filename:.+}")
    public ResponseEntity<Resource> getFoodImage(@PathVariable String filename) {
        try {

            if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
                throw new IllegalArgumentException("Invalid filename");
            }

            Resource resource = resourceLoader.getResource("classpath:images/foods/" + filename);
            if (!resource.exists())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/images/ingredients/{filename:.+}")
    public ResponseEntity<Resource> getIngredientImage(@PathVariable String filename) {
        try {

            if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
                throw new IllegalArgumentException("Invalid filename");
            }

            Resource resource = resourceLoader.getResource("classpath:images/ingredients/" + filename);
            if (!resource.exists())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/images/promotions/{filename:.+}")
    public ResponseEntity<Resource> getPromotionImage(@PathVariable String filename) {
        try {

            if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
                throw new IllegalArgumentException("Invalid filename");
            }

            Resource resource = resourceLoader.getResource("classpath:images/promotions/" + filename);
            if (!resource.exists())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
