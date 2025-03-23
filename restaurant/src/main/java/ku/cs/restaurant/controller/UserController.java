package ku.cs.restaurant.controller;

import ku.cs.restaurant.dto.ApiResponse;
import ku.cs.restaurant.dto.user.UserResponse;
import ku.cs.restaurant.entity.User;
import ku.cs.restaurant.service.UserService;
import ku.cs.restaurant.utils.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class UserController {
    private final UserService userService;
    private final JwtUtils jwtUtils;

    public UserController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> customers = new ArrayList<>(userService.getAllCustomers());
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully.", customers));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable UUID id) {
        Optional<User> optionalUser = userService.getUserById(id);
        return optionalUser.map(user -> ResponseEntity.ok(new ApiResponse<>(true, "User retrieved successfully.", user)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "User not found.", null)));
    }

    @GetMapping("/user/jwt")
    public ResponseEntity<ApiResponse<UserResponse>> getUsernameByJwt(@RequestHeader("Authorization") String jwt) {
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        Optional<User> optionalUser = userService.getUserByUsername(username);
        UserResponse res = new UserResponse();
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            res.setUsername(user.getUsername());
            res.setId(user.getId());
            res.setRole(user.getRole());
            res.setEmail(user.getEmail());
            res.setPhone(user.getPhone());
            return ResponseEntity.ok(new ApiResponse<>(true, "User details retrieved successfully.", res));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, "User not found.", null));
    }
}
