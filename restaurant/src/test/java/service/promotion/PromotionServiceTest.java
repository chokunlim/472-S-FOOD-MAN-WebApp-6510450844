package ku.cs.restaurant.service;

import ku.cs.restaurant.entity.Promotion;
import ku.cs.restaurant.dto.promotion.PromotionCreateRequest;
import ku.cs.restaurant.repository.PromotionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromotionServiceTest {

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private PromotionFoodService promotionFoodService;

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PromotionService promotionService;


    @Test
    void testCreatePromotion_Success() throws InterruptedException {
        // Arrange
        PromotionCreateRequest request = new PromotionCreateRequest();
        request.setName("Promo 1");
        request.setDescription("Special Discount");
        request.setPrice(99.99);
        request.setStartDate(LocalDate.of(2025, 3, 1));
        request.setEndDate(LocalDate.of(2025, 3, 31));
        request.setFoodIds(List.of(UUID.randomUUID(), UUID.randomUUID()));

        Promotion savedPromotion = new Promotion();
        savedPromotion.setId(UUID.randomUUID());
        savedPromotion.setName(request.getName());
        savedPromotion.setDescription(request.getDescription());
        savedPromotion.setPrice(request.getPrice());
        savedPromotion.setStartDate(request.getStartDate());
        savedPromotion.setEndDate(request.getEndDate());
        savedPromotion.setImagePath("test-image.jpg");

        // Mock
        when(promotionRepository.save(any(Promotion.class))).thenReturn(savedPromotion);
        doNothing().when(promotionFoodService).addFoodToPromotion(any(UUID.class), any(UUID.class));
        when(userService.getAllUserEmails()).thenReturn(List.of("test@example.com"));
        doNothing().when(emailService).newPromotion(anyString());

        // Act
        Promotion result = promotionService.createPromotion(request, "test-image.jpg");

        // Assert
        assertNotNull(result);
        assertEquals("Promo 1", result.getName());
        assertEquals("Special Discount", result.getDescription());
        assertEquals(99.99, result.getPrice());
        assertEquals(LocalDate.of(2025, 3, 1), result.getStartDate());
        assertEquals(LocalDate.of(2025, 3, 31), result.getEndDate());

        verify(promotionRepository, times(1)).save(any(Promotion.class));
        verify(promotionFoodService, times(2)).addFoodToPromotion(any(UUID.class), any(UUID.class));
        verify(emailService, times(1)).newPromotion("test@example.com");
    }

    @Test
    void testGetPromotionById() {
        // Arrange
        UUID promoId = UUID.randomUUID();
        Promotion promo = new Promotion();
        promo.setId(promoId);
        promo.setName("Promo 1");

        when(promotionRepository.findById(promoId)).thenReturn(Optional.of(promo));

        // Act
        Optional<Promotion> result = promotionService.getPromotionById(promoId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Promo 1", result.get().getName());
    }

    @Test
    void testGetAllPromotions() {
        // Arrange
        Promotion promo1 = new Promotion();
        promo1.setId(UUID.randomUUID());
        promo1.setName("Promo 1");

        Promotion promo2 = new Promotion();
        promo2.setId(UUID.randomUUID());
        promo2.setName("Promo 2");

        when(promotionRepository.findAll()).thenReturn(List.of(promo1, promo2));

        // Act
        List<Promotion> promotions = promotionService.getAllPromotions();

        // Assert
        assertNotNull(promotions);
        assertEquals(2, promotions.size());
        assertEquals("Promo 1", promotions.get(0).getName());
        assertEquals("Promo 2", promotions.get(1).getName());
    }

    @Test
    void testDeletePromotion_Success() {
        // Arrange
        UUID promoId = UUID.randomUUID();
        Promotion promo = new Promotion();
        promo.setId(promoId);
        promo.setName("Promo to delete");

        // Mock findById to return the promotion
        when(promotionRepository.findById(promoId)).thenReturn(Optional.of(promo));
        doNothing().when(promotionRepository).deleteById(promoId);

        // Act
        boolean result = promotionService.deletePromotion(promoId);

        // Assert
        assertTrue(result);  // คาดหวังว่า result จะเป็น true (ลบสำเร็จ)
        verify(promotionRepository, times(1)).deleteById(promoId);  // ตรวจสอบว่า deleteById ถูกเรียก 1 ครั้ง
    }


    @Test
    void testDeletePromotion_NotFound() {
        // Arrange
        UUID promoId = UUID.randomUUID();

        when(promotionRepository.findById(promoId)).thenReturn(Optional.empty());

        // Act
        boolean result = promotionService.deletePromotion(promoId);

        // Assert
        assertFalse(result);
        verify(promotionRepository, never()).deleteById(promoId);
    }
    @Test
    void testUpdatePromotion_Success() throws InterruptedException {
        // Arrange
        PromotionCreateRequest promotionCreateRequest = new PromotionCreateRequest();
        promotionCreateRequest.setName("Promo 1");
        promotionCreateRequest.setDescription("Special Discount");
        promotionCreateRequest.setPrice(99.99);
        promotionCreateRequest.setStartDate(LocalDate.of(2025, 3, 1));
        promotionCreateRequest.setEndDate(LocalDate.of(2025, 3, 31));
        promotionCreateRequest.setFoodIds(List.of(UUID.randomUUID(), UUID.randomUUID()));

        Promotion savedPromotion = new Promotion();
        savedPromotion.setId(UUID.randomUUID());
        savedPromotion.setName(promotionCreateRequest.getName());
        savedPromotion.setDescription(promotionCreateRequest.getDescription());
        savedPromotion.setPrice(promotionCreateRequest.getPrice());
        savedPromotion.setStartDate(promotionCreateRequest.getStartDate());
        savedPromotion.setEndDate(promotionCreateRequest.getEndDate());
        savedPromotion.setImagePath("test-image.jpg");

        UUID promoId = savedPromotion.getId();

        assertNotNull(promotionCreateRequest);

        // ข้อมูลใหม่สำหรับการอัปเดต
        PromotionCreateRequest request = new PromotionCreateRequest();
        request.setName("Updated Promo");
        request.setDescription("Updated Special Discount");
        request.setPrice(89.99);
        request.setStartDate(LocalDate.of(2025, 4, 1));
        request.setEndDate(LocalDate.of(2025, 4, 30));
        request.setFoodIds(List.of(UUID.randomUUID(), UUID.randomUUID()));

        // Mock: ทำให้ `promotionRepository.findById` คืนค่าโปรโมชั่นเดิมที่มีอยู่
        when(promotionRepository.findById(promoId)).thenReturn(Optional.of(savedPromotion));

        // สร้างโปรโมชั่นใหม่ที่มีการอัปเดต
        Promotion updatedPromotion = new Promotion();
        updatedPromotion.setId(promoId);
        updatedPromotion.setName(request.getName());
        updatedPromotion.setDescription(request.getDescription());
        updatedPromotion.setPrice(request.getPrice());
        updatedPromotion.setStartDate(request.getStartDate());
        updatedPromotion.setEndDate(request.getEndDate());
        updatedPromotion.setImagePath("dummy-image-path");

        // Mock: ทำให้ `promotionRepository.save` คืนค่าการอัปเดตโปรโมชั่น
        when(promotionRepository.save(any(Promotion.class))).thenReturn(updatedPromotion);

        // Act: เรียกฟังก์ชัน `updatePromotion` ใน `promotionService`
        Promotion result = promotionService.updatePromotion(promoId, request, "dummy-image-path");

        // Assert
        assertNotNull(result);
        assertEquals("Updated Promo", result.getName());
        assertEquals("Updated Special Discount", result.getDescription());
        assertEquals(89.99, result.getPrice());
        assertEquals(LocalDate.of(2025, 4, 1), result.getStartDate());
        assertEquals(LocalDate.of(2025, 4, 30), result.getEndDate());
        assertEquals("dummy-image-path", result.getImagePath()); // ตรวจสอบว่าพาธของรูปภาพอัปเดตแล้ว

        // ตรวจสอบว่า `promotionRepository.save()` ถูกเรียกหรือไม่
        verify(promotionRepository, times(1)).save(any(Promotion.class));
    }

}
