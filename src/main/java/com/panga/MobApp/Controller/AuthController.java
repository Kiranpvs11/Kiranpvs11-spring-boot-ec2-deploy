package com.panga.MobApp.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.panga.MobApp.Models.User;
import com.panga.MobApp.Repository.UserRepository;
import com.panga.MobApp.Security.JwtService;
import com.panga.MobApp.Services.CustomUserDetailsService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000") // You can use "*" in dev
public class AuthController {

    private final CustomUserDetailsService userService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthController(
        CustomUserDetailsService userService,
        UserRepository userRepository,
        JwtService jwtService
    ) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        String response = userService.registerUser(user);
        if (response.equals("User already exists!")) {
            return ResponseEntity.badRequest().body(Map.of("error", response));
        }
        return ResponseEntity.ok(Map.of("message", response));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }

        User user = userOpt.get();
        if (!userService.validateUser(username, password)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid password"));
        }

        // ✅ Load UserDetails and generate token with roles
        UserDetails userDetails = userService.loadUserByUsername(username);
        String token = jwtService.generateToken(userDetails); // now includes authorities

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful!");
        response.put("username", user.getUsername());
        response.put("role", user.getAdmin() == 1 ? "admin" : "user");
        response.put("token", token);

        return ResponseEntity.ok(response);
    }
}
