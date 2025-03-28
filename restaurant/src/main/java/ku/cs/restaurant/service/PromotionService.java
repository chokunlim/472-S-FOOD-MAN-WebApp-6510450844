package ku.cs.restaurant.service;

import ku.cs.restaurant.dto.promotion.PromotionCreateRequest;
import ku.cs.restaurant.entity.Promotion;
import ku.cs.restaurant.repository.PromotionRepository;
import ku.cs.restaurant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionService {
    private static final Logger logger = LoggerFactory.getLogger(PromotionService.class);
    private final PromotionRepository promotionRepository;
    private final PromotionFoodService promotionFoodService;
    private final UserService userService;
    private final EmailService emailService;

    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    public Optional<Promotion> getPromotionById(UUID id) {
        return promotionRepository.findById(id);
    }

    public Promotion createPromotion(PromotionCreateRequest request, String imagePath) throws InterruptedException {
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

        List<String> userEmails = userService.getAllUserEmails();
        for (String email : userEmails) {
            System.out.println(email);
            emailService.newPromotion(email.trim());
            Thread.sleep(60000);
        }

        return savedPromotion;
    }

    public boolean deletePromotion(UUID id) {
        Optional<Promotion> promotionOpt = promotionRepository.findById(id);
        if (promotionOpt.isPresent()) {
            Promotion promotion = promotionOpt.get();

            // ลบไฟล์ภาพ (ถ้ามี)
            if (promotion.getImagePath() != null) {
                try {
                    Files.deleteIfExists(Paths.get(promotion.getImagePath()));
                    logger.info("Deleted image: {}", promotion.getImagePath());
                } catch (Exception e) {
                    logger.error("Failed to delete image: {}", promotion.getImagePath(), e);
                }
            }

            // ลบข้อมูลโปรโมชั่นจากฐานข้อมูล
            promotionRepository.deleteById(id);
            return true;  // ลบสำเร็จ
        } else {
            logger.warn("Promotion ID {} not found, skipping deletion.", id);
            return false;  // ไม่พบโปรโมชั่น
        }
    }
    public Promotion updatePromotion(UUID id, PromotionCreateRequest request, String imagePath) {
        Optional<Promotion> promotionOpt = promotionRepository.findById(id);
        if (promotionOpt.isPresent()) {
            Promotion promotion = promotionOpt.get();
            promotion.setName(request.getName());
            promotion.setDescription(request.getDescription());
            promotion.setPrice(request.getPrice());
            promotion.setStartDate(request.getStartDate());
            promotion.setEndDate(request.getEndDate());

            // อัปเดตพาธรูปภาพ ถ้ามี
            if (imagePath != null) {
                promotion.setImagePath(imagePath);
            }

            return promotionRepository.save(promotion);
        } else {
            throw new IllegalArgumentException("Promotion not found with id: " + id);
        }
    }
    public List<Promotion> getActivePromotions() {
        // ใช้วันที่ปัจจุบันในการกรองโปรโมชั่นที่ยังไม่หมดอายุ
        return promotionRepository.findAll().stream()
                .filter(promotion -> promotion.getEndDate().isAfter(LocalDate.now()))
                .collect(Collectors.toList());
    }
    public Promotion getPromotionDetails(UUID promoId) {
        // ค้นหาข้อมูลโปรโมชั่นจากฐานข้อมูลโดยใช้ promoId
        return promotionRepository.findById(promoId)
                .orElse(null);  // ถ้าไม่พบโปรโมชั่นให้คืนค่า null
    }

}