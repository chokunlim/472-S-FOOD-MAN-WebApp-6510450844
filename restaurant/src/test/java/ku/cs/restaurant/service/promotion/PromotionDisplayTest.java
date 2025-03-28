package ku.cs.restaurant.service.promotion;

import ku.cs.restaurant.entity.Promotion;
import ku.cs.restaurant.repository.PromotionRepository;
import ku.cs.restaurant.service.PromotionService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PromotionDisplayTest {
    @Mock
    private PromotionRepository promotionRepository;

    @InjectMocks
    private PromotionService promotionService;
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
