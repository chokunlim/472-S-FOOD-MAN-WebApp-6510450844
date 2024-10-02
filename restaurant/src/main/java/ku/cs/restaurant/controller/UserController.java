package ku.cs.restaurant.controller;

import ku.cs.restaurant.entity.User;
import ku.cs.restaurant.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllCustomers() {
        List<User> customers = new ArrayList<>(userService.getAllCustomers());

        return new ResponseEntity<>(customers, HttpStatus.OK);
    }
}
