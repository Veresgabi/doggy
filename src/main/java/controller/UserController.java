package controller;

import dto.DogResponse;
import dto.FilterDogDTO;
import model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import repository.UserRepository;
import service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import util.SecurityCipher;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @PostMapping(value = "/add-admin")
    public ResponseEntity<String> addAdmin(@RequestBody User user) {

        try {
            if (userRepository.findAll().isEmpty()) {
                user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
                user.setEnabled(true);
                userRepository.save(user);
                return ResponseEntity.ok("Admin added.");
            }
            else {
                return ResponseEntity.unprocessableEntity().body("Admin already exists in DB.");
            }
        }
        catch (Exception exception) {
            return ResponseEntity.internalServerError().body(exception.getMessage() != null ? exception.getMessage() : exception.toString());
        }

    }
}
