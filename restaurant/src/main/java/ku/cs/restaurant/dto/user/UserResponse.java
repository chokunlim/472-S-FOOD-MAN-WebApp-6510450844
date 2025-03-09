package ku.cs.restaurant.dto.user;

import lombok.Data;

import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private String username;
    private String phone;
    private String email;
    private String role;
}
