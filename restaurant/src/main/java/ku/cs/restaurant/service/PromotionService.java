package ku.cs.restaurant.service;

import ku.cs.restaurant.dto.promotion.PromotionCreateRequest;
import ku.cs.restaurant.entity.Promotion;
import ku.cs.restaurant.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PromotionService {
    private static final Logger logger = LoggerFactory.getLogger(PromotionService.class);
    private final PromotionRepository promotionRepository;
    private final ImageService imageService;

    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    public Optional<Promotion> getPromotionById(UUID id) {
        return promotionRepository.findById(id);
    }

    public Promotion createPromotion(PromotionCreateRequest request, String imagePath) {
        Promotion promotion = new Promotion();
        promotion.setName(request.getName());
        promotion.setDescription(request.getDescription());
        promotion.setType(request.getType());
        promotion.setDiscountValue(request.getDiscountValue());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setImagePath(imagePath);

        return promotionRepository.save(promotion);
    }

    public void deletePromotion(UUID id) {
        Optional<Promotion> promotionOpt = promotionRepository.findById(id);
        if (promotionOpt.isPresent()) {
            Promotion promotion = promotionOpt.get();

            if (promotion.getImagePath() != null) {
                try {
                    Files.deleteIfExists(Paths.get(promotion.getImagePath()));
                    logger.info("Deleted image: {}", promotion.getImagePath());
                } catch (IOException e) {
                    logger.error("Failed to delete image: {}", promotion.getImagePath(), e);
                }
            }

            promotionRepository.deleteById(id);
        } else {
            logger.warn("Promotion ID {} not found, skipping deletion.", id);
        }
    }
}
