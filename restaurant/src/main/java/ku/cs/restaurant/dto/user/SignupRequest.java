package ku.cs.restaurant.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 8, max = 20, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank
    private String confirmPassword;

    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 characters long")
    @Pattern(regexp = "\\d+", message = "Phone number must only contain digits")
    private String phone;

    @NotBlank
    @Email(message = "Invalid email format")
    private String email;

    private String role;
}

