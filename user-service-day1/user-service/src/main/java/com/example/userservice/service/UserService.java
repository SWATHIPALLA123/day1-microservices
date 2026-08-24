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