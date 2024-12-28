package com.example.demo.controllers;

import com.example.demo.services.JwtUtil;
import com.example.demo.services.Userservice;
import com.example.demo.tables.User;
import com.example.demo.tables.Usertype;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class Usercontroller {

    private final Userservice userService;
    private final JwtUtil jwtUtil;

@PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        try {
            String username = payload.get("username");
            String password = payload.get("password");
            String email = payload.get("email");
            String fullName = payload.get("fullName");
            String type = payload.get("type");

           userService.register(
                    username, password, email, fullName, Usertype.valueOf(type.toUpperCase())
            );

            return ResponseEntity.ok(
                     "Registration successful"
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Registration failed",
                    "error", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
    
        Optional<User> user = userService.login(username, password);
    
        if (user.isPresent()) {
            return jwtUtil.generateToken(user.get());
        } else {
            throw new RuntimeException("Invalid Credentials: " + username);
        }
    }
    
    @GetMapping("/profile")
    public User getProfile(HttpServletRequest request) {
        Long userId = jwtUtil.extractUserId(request);
        return userService.getUserProfile(userId);
    }
    
}
