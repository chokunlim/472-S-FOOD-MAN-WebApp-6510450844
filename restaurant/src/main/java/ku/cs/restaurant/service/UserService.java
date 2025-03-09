package ku.cs.restaurant.service;

import ku.cs.restaurant.dto.user.SignupRequest;
import ku.cs.restaurant.dto.user.SignupResponse;
import ku.cs.restaurant.entity.User;
import ku.cs.restaurant.repository.UserRepository;
import ku.cs.restaurant.dto.ApiResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ApiResponse<SignupResponse> createUser(SignupRequest user) {
        SignupResponse signupResponse = new SignupResponse();

        Optional<User> existedUser = userRepository.findByUsername(user.getUsername());
        Optional<User> existedEmail = userRepository.findByEmail(user.getEmail());

        if (existedUser.isPresent()) {
            signupResponse.setMessage("Username already exists");
            return new ApiResponse<>(false, signupResponse.getMessage(), signupResponse);
        }

        // Check if a user with the given email already exists
        if (existedEmail.isPresent()) {
            signupResponse.setMessage("Email already exists");
            return new ApiResponse<>(false, signupResponse.getMessage(), signupResponse);
        }

        String username = user.getUsername();
        String phone = user.getPhone();
        String password = user.getPassword();
        String confirmPassword = user.getConfirmPassword();
        String role = (user.getRole() != null && !user.getRole().isEmpty()) ? user.getRole() : "CUSTOMER";

        if (!password.equals(confirmPassword)) {
            signupResponse.setMessage("Passwords do not match");
            return new ApiResponse<>(false, signupResponse.getMessage(), signupResponse);
        }

        String encodedPassword = passwordEncoder.encode(password);

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPhone(phone);
        newUser.setPassword(encodedPassword);
        newUser.setEmail(user.getEmail());
        newUser.setRole(role);

        userRepository.save(newUser);

        signupResponse.setMessage("User created");
        return new ApiResponse<>(true, signupResponse.getMessage(), signupResponse);
    }

    public List<User> getAllCustomers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }
}
