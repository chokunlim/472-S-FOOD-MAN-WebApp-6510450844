package ku.cs.restaurant.controller;

import ku.cs.restaurant.dto.Payment.PaymentResponse;
import ku.cs.restaurant.dto.financial.CreateFinancialRequest;
import ku.cs.restaurant.dto.food.FoodDto;
import ku.cs.restaurant.dto.food.FoodListDto;
import ku.cs.restaurant.dto.order.*;
import ku.cs.restaurant.entity.*;
import ku.cs.restaurant.service.*;
import ku.cs.restaurant.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;  // Injected UserService
    private final FinancialService financialService; // Injected FinancialService
    private final IngredientService ingredientService;

    @PostMapping("/order")
    public ResponseEntity<ApiResponse<PaymentResponse>> createOrder(
            @RequestBody OrderRequest orderRequest,
            @RequestHeader("Authorization") String jwt) {

        try {
            // Call the OrderService to create order and calculate total
            PaymentResponse paymentResponse = orderService.createOrder(orderRequest, jwt);
            return ResponseEntity.status(201)
                    .body(new ApiResponse<>(true, "Order created successfully.", paymentResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "An error occurred: " + e.getMessage(), null));
        }
    }

    @GetMapping("/order")
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders() {
        try {
            List<Order> orders = orderService.findOrders();
            return ResponseEntity.ok(new ApiResponse<>(true, "Orders fetched successfully.", orders));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "An error occurred: " + e.getMessage(), null));
        }
    }

    @GetMapping("/order/{id}/user")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByOrderId(@PathVariable("id") UUID id) {
        try {
            Order order = orderService.findOrderById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));
            User user = userService.getUserById(order.getUser().getId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            UserResponse userResponse = new UserResponse();
            userResponse.setId(user.getId());
            userResponse.setUsername(user.getUsername());
            userResponse.setPhone(user.getPhone());
            userResponse.setRole(user.getRole());

            return ResponseEntity.ok(new ApiResponse<>(true, "User fetched successfully.", userResponse));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "An error occurred: " + e.getMessage(), null));
        }
    }

    @GetMapping("/order/{id}/receipt")
    public ResponseEntity<ApiResponse<Receipt>> getReceiptByOrderId(@PathVariable("id") UUID id) {
        try {
            Order order = orderService.findOrderById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));
            Receipt receipt = order.getReceipt();
            return ResponseEntity.ok(new ApiResponse<>(true, "Receipt fetched successfully.", receipt));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "An error occurred: " + e.getMessage(), null));
        }
    }

    @GetMapping("/order/{id}/food")
    public ResponseEntity<ApiResponse<FoodListDto>> getFoodByOrderId(@PathVariable("id") UUID id) {
        try {
            Order order = orderService.findOrderById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));

            List<OrderLine> orderLines = order.getOrderLines();
            FoodListDto foodListDto = new FoodListDto();

            List<FoodDto> foodDtos = orderLines.stream()
                    .map(ol -> {
                        FoodDto foodDto = new FoodDto();
                        foodDto.setFood(ol.getFood());
                        foodDto.setQty(ol.getQty());
                        return foodDto;
                    })
                    .collect(Collectors.toList());

            foodListDto.setFoods(foodDtos);

            return ResponseEntity.ok(new ApiResponse<>(true, "Foods fetched successfully.", foodListDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "An error occurred: " + e.getMessage(), null));
        }
    }

    @GetMapping("/order/status")
    public ResponseEntity<ApiResponse<List<Order>>> getOrdersByStatus(@RequestParam OrderStatus status) {
        try {
            List<Order> orders = orderService.findOrderByStatus(status);
            return ResponseEntity.ok(new ApiResponse<>(true, "Orders fetched successfully.", orders));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "An error occurred: " + e.getMessage(), null));
        }
    }

    @PatchMapping("/order")
    public ResponseEntity<ApiResponse<Order>> updateOrderStatusById(@RequestBody UpdateStatusRequest request) {
        try {
            Optional<Order> optionalUpdatedOrder = orderService.updateOrderStatusById(request.getId(), request.getStatus());
            if (optionalUpdatedOrder.isPresent()) {
                Order updatedOrder = optionalUpdatedOrder.get();
                return ResponseEntity.ok(new ApiResponse<>(true, "Order status updated successfully.", updatedOrder));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Order not found.", null));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "An error occurred: " + e.getMessage(), null));
        }
    }

    @PostMapping("/order/ingredient/{id}")
    public void updateOrderIngredientQty(@PathVariable UUID id) {
        Order order = orderService.findOrderById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus().equals(OrderStatus.COMPLETE)) {
            throw new IllegalStateException("Cannot update ingredients for completed order.");
        }

        CreateFinancialRequest req = new CreateFinancialRequest();
        Optional<Order> existOrder = orderService.findOrderById(id);
        if (existOrder.isPresent()) {
            req.setIncome(existOrder.get().getTotal());
            req.setExpense(0.0);
            financialService.addFinancial(req);
        }

        List<OrderLine> orderLines = order.getOrderLines();

        for (OrderLine ol : orderLines) {
            int foodOrderedQty = ol.getQty();
            Food food = ol.getFood();
            List<Recipe> recipes = food.getRecipes();

            for (Recipe r : recipes) {
                int ingredientUsedQty = r.getQty();
                int totalUsed = foodOrderedQty * ingredientUsedQty;

                // Update the ingredient quantity
                ingredientService.updateQty(r.getIngredient().getId(), r.getIngredient().getQty() - totalUsed);
            }
        }
    }
}