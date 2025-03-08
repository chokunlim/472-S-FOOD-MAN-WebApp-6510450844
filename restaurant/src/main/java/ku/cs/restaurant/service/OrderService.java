package ku.cs.restaurant.service;

import jakarta.transaction.Transactional;
import ku.cs.restaurant.dto.Payment.PaymentResponse;
import ku.cs.restaurant.dto.order.FoodOrder;
import ku.cs.restaurant.dto.order.OrderRequest;
import ku.cs.restaurant.dto.order.PromotionOrder;
import ku.cs.restaurant.entity.*;
import ku.cs.restaurant.repository.IngredientRepository;
import ku.cs.restaurant.repository.OrderLineRepository;
import ku.cs.restaurant.repository.UserRepository;
import ku.cs.restaurant.utils.JwtUtils;
import ku.cs.restaurant.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final PromotionService promotionService;
    private final OrderLineRepository orderLineRepository;
    private final IngredientRepository ingredientRepository;
    private final FoodService foodService;
    private final ReceiptService receiptService;

    public Optional<Order> findOrderById(UUID id) {
        return orderRepository.findById(id);
    }

    // Create new order
    @Transactional
    public PaymentResponse createOrder(OrderRequest orderRequest, String jwt) throws Exception {
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new Exception("User not found.");
        }

        User user = optionalUser.get();
        double total = calculateTotal(orderRequest);

        // Create order and save it
        Order order = new Order();
        order.setUser(user);
        order.setTotal(total);
        orderRepository.save(order);

        // Process food orders
        if (!orderRequest.getFoods().isEmpty()) {
            for (FoodOrder foodOrder : orderRequest.getFoods()) {
                processFoodOrder(foodOrder, order);
            }
        }

        // Process promotions
        if (!orderRequest.getPromotions().isEmpty()) {
            for (PromotionOrder promotionOrder : orderRequest.getPromotions()) {
                Promotion promotion = promotionService.getPromotionById(promotionOrder.getPromotionId())
                        .orElseThrow(() -> new IllegalArgumentException("Cannot find promotion with ID: " + promotionOrder.getPromotionId()));

                // Loop through the promotion foods and create order lines for each
                for (PromotionFood promotionFood : promotion.getPromotionFoods()) {
                    Food food = promotionFood.getFood();

                    // Create order line for each food in the promotion
                    OrderLine orderLine = new OrderLine();
                    OrderLineKey orderLineKey = new OrderLineKey();
                    orderLineKey.setFoodId(food.getId());
                    orderLineKey.setOrderId(order.getId());
                    orderLine.setId(orderLineKey);
                    orderLine.setOrder(order);
                    orderLine.setFood(food);  // Set the food from the promotion
                    orderLine.setQty(promotionOrder.getQuantity());  // Quantity from the promotion order

                    // Save the order line for promotion food
                    orderLineRepository.save(orderLine);
                }
            }
        }

        // Create Receipt
        Receipt receipt = receiptService.createReceipt(order.getTotal());
        order.setReceipt(receipt);

        // Create payment link
        PaymentResponse response = paymentService.createPaymentLink(order);
        order.setPaymentLink(response.getPaymentLink());

        orderRepository.save(order);

        return response;
    }

    private void processFoodOrder(FoodOrder foodOrder, Order order) throws Exception {
        // Retrieve the food entity using the foodService and food ID from the request
        Food food = foodService.getFoodById(foodOrder.getFoodId())
                .orElseThrow(() -> new Exception("Can't find food with ID: " + foodOrder.getFoodId()));

        // Create order lines based on food order details
        OrderLine orderLine = new OrderLine();
        OrderLineKey orderLineKey = new OrderLineKey();
        orderLineKey.setFoodId(foodOrder.getFoodId());
        orderLineKey.setOrderId(order.getId());
        orderLine.setId(orderLineKey);
        orderLine.setOrder(order);
        orderLine.setFood(food);  // Set the actual Food entity
        orderLine.setQty(foodOrder.getQuantity());

        // Save the order line
        orderLineRepository.save(orderLine);
    }

    private double calculateTotal(OrderRequest orderRequest) {
        double total = 0;

        // Calculate total for food orders
        for (FoodOrder foodOrder : orderRequest.getFoods()) {
            // Fetch the food entity and calculate the total based on quantity
            Food food = foodService.getFoodById(foodOrder.getFoodId())
                    .orElseThrow(() -> new RuntimeException("Can't find food with ID: " + foodOrder.getFoodId()));

            total += foodOrder.getQuantity() * food.getPrice();
        }

        // Calculate total for promotions
        for (PromotionOrder promotionOrder : orderRequest.getPromotions()) {
            // Fetch the promotion entity and calculate the total based on quantity
            Promotion promotion = promotionService.getPromotionById(promotionOrder.getPromotionId())
                    .orElseThrow(() -> new RuntimeException("Can't find promotion with ID: " + promotionOrder.getPromotionId()));

            total += promotionOrder.getQuantity() * promotion.getPrice();
        }

        return total;
    }

    // View all orders
    public List<Order> findOrders() {
        return orderRepository.findAll();
    }

    // View orders by status
    public List<Order> findOrderByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    // Update order status
    public Optional<Order> updateOrderStatusById(UUID id, OrderStatus status) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        optionalOrder.ifPresent(order -> {
            order.setStatus(status);
            orderRepository.save(order);
        });

        return optionalOrder;
    }
}