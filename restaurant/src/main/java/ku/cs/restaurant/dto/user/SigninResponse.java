package ku.cs.restaurant.dto.user;

import lombok.Data;

import java.util.UUID;

@Data
public class SigninResponse {
    private String token;
    private UUID id;
    private String username;
    private String phone;
    private String role;
    private String email;
}
