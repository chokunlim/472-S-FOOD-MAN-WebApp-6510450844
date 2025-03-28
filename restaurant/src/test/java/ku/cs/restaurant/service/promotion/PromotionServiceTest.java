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


    //ร้านค้าสามารถแก้ไขรายละเอียดโปรโมชั่นได้
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

    // ลูกค้าต้องเห็นโปรโมชั่นทั้งหมดที่ยังไม่หมดอายุ
    @Test
    void testGetActivePromotions() {
        // Arrange
        LocalDate now = LocalDate.now();

        Promotion promo1 = new Promotion();
        promo1.setId(UUID.randomUUID());
        promo1.setName("Active Promo 1");
        promo1.setDescription("Discount 20%");
        promo1.setPrice(100.0);
        promo1.setStartDate(now.minusDays(5));
        promo1.setEndDate(now.plusDays(5));

        Promotion promo2 = new Promotion();
        promo2.setId(UUID.randomUUID());
        promo2.setName("Expired Promo");
        promo2.setDescription("Discount 10%");
        promo2.setPrice(50.0);
        promo2.setStartDate(now.minusDays(10));
        promo2.setEndDate(now.minusDays(2));

        // Mock: ให้ `promotionRepository.findAll()` คืนค่ารายการโปรโมชั่นทั้งหมด
        when(promotionRepository.findAll()).thenReturn(List.of(promo1, promo2));

        // Act
        List<Promotion> result = promotionService.getActivePromotions();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());  // คาดว่าผลลัพธ์จะมีแค่โปรโมชั่นที่ยังไม่หมดอายุ
        assertEquals("Active Promo 1", result.get(0).getName());
    }
    //โปรโมชั่นต้องแสดงข้อมูลครบถ้วน เช่น ส่วนลดและเงื่อนไข
    @Test
    void testGetPromotionDetails_WithCompleteInfo() {
        // Arrange
        UUID promoId = UUID.randomUUID();
        Promotion promo = new Promotion();
        promo.setId(promoId);
        promo.setName("Spring Sale");
        promo.setDescription("50% off on all items! Terms and conditions apply.");
        promo.setPrice(50.0);  // ตัวเลขส่วนลด
        promo.setStartDate(LocalDate.of(2025, 4, 1));  // วันเริ่มต้นโปรโมชั่น
        promo.setEndDate(LocalDate.of(2025, 4, 30));  // วันสิ้นสุดโปรโมชั่น

        // Mock: ค่าผลลัพธ์จาก promotionRepository.findById
        when(promotionRepository.findById(promoId)).thenReturn(Optional.of(promo));

        // Act
        Promotion result = promotionService.getPromotionDetails(promoId);

        // Assert
        assertNotNull(result);  // ตรวจสอบว่า result ไม่เป็น null
        assertEquals(promoId, result.getId());  // ตรวจสอบว่า ID ตรงกัน
        assertEquals("Spring Sale", result.getName());  // ตรวจสอบชื่อโปรโมชั่น
        assertEquals("50% off on all items! Terms and conditions apply.", result.getDescription());  // ตรวจสอบรายละเอียดโปรโมชั่น
        assertEquals(50.0, result.getPrice());  // ตรวจสอบส่วนลด
        assertEquals(LocalDate.of(2025, 4, 1), result.getStartDate());  // ตรวจสอบวันที่เริ่มต้นโปรโมชั่น
        assertEquals(LocalDate.of(2025, 4, 30), result.getEndDate());  // ตรวจสอบวันที่สิ้นสุดโปรโมชั่น

        // ตรวจสอบว่าเงื่อนไขของโปรโมชั่นที่เกี่ยวข้อง
        String expectedConditions = "Terms and conditions apply.";
        assertTrue(result.getDescription().contains(expectedConditions));  // ตรวจสอบว่าเงื่อนไขอยู่ในคำอธิบาย

        // ตรวจสอบว่า promotionRepository.findById ถูกเรียก 1 ครั้ง
        verify(promotionRepository, times(1)).findById(promoId);
    }

    // เมื่อลูกค้าคลิกโปรโมชั่น ต้องแสดงรายละเอียดเพิ่มเติม
    @Test
    void testGetPromotionDetails_Success() {
        // Arrange
        UUID promoId = UUID.randomUUID();
        Promotion promo = new Promotion();
        promo.setId(promoId);
        promo.setName("Spring Sale");
        promo.setDescription("50% off on all items!");
        promo.setPrice(50.0);
        promo.setStartDate(LocalDate.of(2025, 4, 1));
        promo.setEndDate(LocalDate.of(2025, 4, 30));

        // Mock: ค่าผลลัพธ์จาก promotionRepository.findById
        when(promotionRepository.findById(promoId)).thenReturn(Optional.of(promo));

        // Act
        Promotion result = promotionService.getPromotionDetails(promoId);

        // Assert
        assertNotNull(result);  // ตรวจสอบว่าไม่เป็น null
        assertEquals(promoId, result.getId());  // ตรวจสอบว่า ID ตรงกัน
        assertEquals("Spring Sale", result.getName());  // ตรวจสอบชื่อโปรโมชั่น
        assertEquals("50% off on all items!", result.getDescription());  // ตรวจสอบรายละเอียดโปรโมชั่น
        assertEquals(50.0, result.getPrice());  // ตรวจสอบราคาส่วนลด
        assertEquals(LocalDate.of(2025, 4, 1), result.getStartDate());  // ตรวจสอบวันที่เริ่ม
        assertEquals(LocalDate.of(2025, 4, 30), result.getEndDate());  // ตรวจสอบวันที่สิ้นสุด

        // ตรวจสอบว่า promotionRepository.findById ถูกเรียก 1 ครั้ง
        verify(promotionRepository, times(1)).findById(promoId);
    }

    @Test
    void testGetPromotionDetails_NotFound() {
        // Arrange
        UUID promoId = UUID.randomUUID();

        // Mock: ไม่มีโปรโมชั่นในฐานข้อมูลที่มี ID ตรงกัน
        when(promotionRepository.findById(promoId)).thenReturn(Optional.empty());

        // Act
        Promotion result = promotionService.getPromotionDetails(promoId);

        // Assert
        assertNull(result);  // คาดหวังว่า result จะเป็น null เพราะไม่พบข้อมูลโปรโมชั่น

        // ตรวจสอบว่า promotionRepository.findById ถูกเรียก 1 ครั้ง
        verify(promotionRepository, times(1)).findById(promoId);
    }
}
