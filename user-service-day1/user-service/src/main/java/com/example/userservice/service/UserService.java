package com.example.userservice.service;

import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public UserResponse getUser() {

        return new UserResponse(
                1L,
                "Swathi",
                "swathi@example.com"
        );
    }

    public UserResponse getUserById(Long id) {

        // Wait for 5 seconds to test Order Service timeout
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "User Service request interrupted",
                    e
            );
        }

        return new UserResponse(
                id,
                "Swathi",
                "swathi@example.com"
        );
    }

    public UserResponse createUser(UserRequest userRequest) {

        return new UserResponse(
                userRequest.getId(),
                userRequest.getName(),
                userRequest.getEmail()
        );
    }
}