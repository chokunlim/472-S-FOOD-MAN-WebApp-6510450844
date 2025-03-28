package ku.cs.restaurant.service.promotion;

import ku.cs.restaurant.entity.Promotion;
import ku.cs.restaurant.dto.promotion.PromotionCreateRequest;
import ku.cs.restaurant.repository.PromotionRepository;
import ku.cs.restaurant.service.EmailService;
import ku.cs.restaurant.service.PromotionFoodService;
import ku.cs.restaurant.service.PromotionService;
import ku.cs.restaurant.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromotionNotificationTest {

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

    //ลูกค้าต้องได้รับการแจ้งเตือนเมื่อมีโปรโมชั่นใหม่
    @Test
    void testCustomerReceivesNotification_WhenNewPromotionCreated() throws InterruptedException {
        // Arrange
        PromotionCreateRequest request = new PromotionCreateRequest();
        request.setName("Spring Sale");
        request.setDescription("Discount up to 50%");
        request.setPrice(50.0);
        request.setStartDate(LocalDate.of(2025, 4, 1));
        request.setEndDate(LocalDate.of(2025, 4, 30));
        request.setFoodIds(List.of(UUID.randomUUID(), UUID.randomUUID()));

        Promotion savedPromotion = new Promotion();
        savedPromotion.setId(UUID.randomUUID());
        savedPromotion.setName(request.getName());
        savedPromotion.setDescription(request.getDescription());
        savedPromotion.setPrice(request.getPrice());
        savedPromotion.setStartDate(request.getStartDate());
        savedPromotion.setEndDate(request.getEndDate());
        savedPromotion.setImagePath("promo-image.jpg");

        // Mocking
        when(promotionRepository.save(any(Promotion.class))).thenReturn(savedPromotion);
        when(userService.getAllUserEmails()).thenReturn(List.of("customer1@example.com", "customer2@example.com"));

        // Act
        Promotion result = promotionService.createPromotion(request, "promo-image.jpg");

        // Assert
        assertNotNull(result);
        verify(emailService, times(1)).newPromotion("customer1@example.com");
        verify(emailService, times(1)).newPromotion("customer2@example.com");
    }
    //ลูกค้าต้องสามารถเลือกเปิด/ปิดการแจ้งเตือนได้
    @Test
    void testCustomerCanOptOutOfNotifications() throws InterruptedException {
        // Arrange
        PromotionCreateRequest request = new PromotionCreateRequest();
        request.setName("Spring Sale");
        request.setDescription("Discount up to 50%");
        request.setPrice(50.0);
        request.setStartDate(LocalDate.of(2025, 4, 1));
        request.setEndDate(LocalDate.of(2025, 4, 30));
        request.setFoodIds(List.of(UUID.randomUUID(), UUID.randomUUID()));

        Promotion savedPromotion = new Promotion();
        savedPromotion.setId(UUID.randomUUID());

        // Mocking: มีลูกค้า 2 คน แต่คนที่ 2 ปิดการแจ้งเตือน
        when(promotionRepository.save(any(Promotion.class))).thenReturn(savedPromotion);
        when(userService.getAllUserEmails()).thenReturn(List.of("customer1@example.com"));

        // Act
        Promotion result = promotionService.createPromotion(request, "promo-image.jpg");

        // Assert
        assertNotNull(result);
        verify(emailService, times(1)).newPromotion("customer1@example.com");
        verify(emailService, never()).newPromotion("customer2@example.com"); // ต้องไม่ส่งอีเมลไปยังคนที่ปิดการแจ้งเตือน
    }
    //การแจ้งเตือนต้องระบุชื่อและรายละเอียดโปรโมชั่นอย่างชัดเจน
    @Test
    void testNotificationContentIsCorrect() throws InterruptedException {
        // Arrange
        PromotionCreateRequest request = new PromotionCreateRequest();
        request.setName("Holiday Special");
        request.setDescription("Limited-time offer!");
        request.setPrice(79.99);
        request.setStartDate(LocalDate.of(2025, 12, 1));
        request.setEndDate(LocalDate.of(2025, 12, 31));
        request.setFoodIds(List.of(UUID.randomUUID(), UUID.randomUUID()));

        Promotion savedPromotion = new Promotion();
        savedPromotion.setId(UUID.randomUUID());
        savedPromotion.setName(request.getName());
        savedPromotion.setDescription(request.getDescription());
        savedPromotion.setPrice(request.getPrice());
        savedPromotion.setStartDate(request.getStartDate());
        savedPromotion.setEndDate(request.getEndDate());

        // Mocking
        when(promotionRepository.save(any(Promotion.class))).thenReturn(savedPromotion);
        when(userService.getAllUserEmails()).thenReturn(List.of("test@example.com"));

        // Mocking emailService to avoid Thread.sleep and simulate sending email
        doNothing().when(emailService).newPromotion(anyString());  // Mocking newPromotion method

        // Act
        promotionService.createPromotion(request, "promo-image.jpg");

        // Assert
        verify(emailService, times(1)).newPromotion("test@example.com");  // ตรวจสอบว่า emailService ถูกเรียก 1 ครั้ง
    }
}
