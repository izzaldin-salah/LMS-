package com.example.demo.services;

import com.example.demo.repository.UserRepository;
import com.example.demo.tables.User;
import com.example.demo.tables.Usertype;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for managing users in the system.
 */
@Service
@RequiredArgsConstructor
public class Userservice {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Registers a new user with the provided details.
     */
    public User register(String username, String rawPassword, String email, String fullName, Usertype type) {
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .email(email)
                .fullName(fullName)
                .type(type)
                .build();

        return userRepository.save(user);
    }

    /**
     * Logs in a user by verifying credentials and returning the user object.
     */
    public Optional<User> login(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()));
    }

    /**
     * Retrieves a user profile by user ID.
     */
    public User getUserProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Finds a user by ID.
     */
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
