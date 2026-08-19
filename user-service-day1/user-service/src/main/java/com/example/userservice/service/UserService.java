package com.example.userservice.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.userservice.dto.UserResponse;

@Service
public class UserService {

    private final Map<Long, UserResponse> users = Map.of(
        1L, new UserResponse(1L, "Swathi", "swathi@gmail.com"),
        2L, new UserResponse(2L, "Rahul", "rahul@gmail.com"),
        3L, new UserResponse(3L, "Priya", "priya@gmail.com")
    );

    public UserResponse getUserById(Long id) {
        UserResponse user = users.get(id);

        if (user == null) {
            throw new UserNotFoundException("User not found with id: " + id);
        }

        return user;
    }
}
