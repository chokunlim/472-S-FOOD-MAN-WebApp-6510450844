package ku.cs.restaurant.service;

import ku.cs.restaurant.dto.promotion.PromotionCreateRequest;
import ku.cs.restaurant.entity.Promotion;
import ku.cs.restaurant.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
    private final PromotionFoodService promotionFoodService;

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
        promotion.setPrice(request.getPrice());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setImagePath(imagePath);

        Promotion savedPromotion = promotionRepository.save(promotion);

        // 🔥 เพิ่มอาหารลงในโปรโมชั่นทันที
        if (request.getFoodIds() != null && !request.getFoodIds().isEmpty()) {
            for (UUID foodId : request.getFoodIds()) {
                promotionFoodService.addFoodToPromotion(savedPromotion.getId(), foodId);
            }
        }

        return savedPromotion;
    }

    public boolean deletePromotion(UUID id) {
        Optional<Promotion> promotionOpt = promotionRepository.findById(id);
        if (promotionOpt.isPresent()) {
            Promotion promotion = promotionOpt.get();

            if (promotion.getImagePath() != null) {
                try {
                    Files.deleteIfExists(Paths.get(promotion.getImagePath()));
                    logger.info("Deleted image: {}", promotion.getImagePath());
                } catch (Exception e) {
                    logger.error("Failed to delete image: {}", promotion.getImagePath(), e);
                }
            }

            promotionRepository.deleteById(id);
        } else {
            logger.warn("Promotion ID {} not found, skipping deletion.", id);
        }
        return false;
    }
}