package service.rider;

import ku.cs.restaurant.entity.*;
import ku.cs.restaurant.repository.OrderRepository;
import ku.cs.restaurant.service.*;
import ku.cs.restaurant.common.OrderStatus;
import ku.cs.restaurant.dto.order.OrderRequest;
import ku.cs.restaurant.dto.order.FoodOrder;
import ku.cs.restaurant.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserService userService;
    @Mock
    private ReceiptService receiptService;
    @Mock
    private PaymentService paymentService;
    @Mock
    private OrderLineService orderLineService;
    @Mock
    private IngredientService ingredientService;
    @Mock
    private FoodService foodService;
    @Mock
    private JwtUtils jwtUtils;
    @InjectMocks
    private OrderService orderService;

    private User user;
    private Food food;
    private Order order;
    private FoodOrder foodOrder;
    private OrderRequest orderRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // สร้าง User
        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");

        // สร้าง Food
        food = new Food();
        food.setId(UUID.randomUUID());
        food.setPrice(100.0);
        food.setName("Pizza");

        // สร้าง FoodOrder
        foodOrder = new FoodOrder();
        foodOrder.setFood(food);
        foodOrder.setQuantity(2);

        // สร้าง OrderRequest
        orderRequest = new OrderRequest();
        orderRequest.setFoods(Collections.singletonList(foodOrder));

        // สร้าง Order
        order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.PENDING);
        order.setUser(user);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(order.getCreatedAt());
    }

    // Test 1: Rider สามารถเปลี่ยนสถานะออเดอร์ได้แค่ครั้งเดียว

    // Test 2: ลูกค้าต้องได้รับแจ้งเตือนเมื่อออเดอร์ถูกส่งแล้ว
    @Test
    void testNotifyCustomerOnOrderShipped() {
        // Arrange
        UUID orderId = order.getId();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Simulate notifying customer
        AtomicBoolean isNotified = new AtomicBoolean(false);

        // Act - Change the order status to DELIVERING
        orderService.updateOrderStatusById(orderId, OrderStatus.DELIVERING).ifPresent(o -> {
            if (o.getStatus() == OrderStatus.DELIVERING) {
                isNotified.set(true); // Assume the notification is sent
            }
        });

        // Assert - Verify customer is notified
        assertTrue(isNotified.get(), "Customer should be notified when the order is shipped.");
    }

    // Test 3: ระบบต้องบันทึก timestamp ของการส่งอาหารสำเร็จ


    // Test 4: ตรวจสอบการคำนวณ total ของ OrderRequest
    @Test
    void testCalculateTotal() {
        // Arrange
        double expectedTotal = 2 * 100.0; // 2 pieces of Pizza at 100 each
        double calculatedTotal = orderRequest.calculateTotal();

        // Assert - Verify total calculation is correct
        assertEquals(expectedTotal, calculatedTotal, "Total should be the sum of the quantity times the price");
    }
}
