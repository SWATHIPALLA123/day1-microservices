package com.example.userservice.controller;

import com.example.userservice.dto.UserResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/api/users/{id}")
    public UserResponse getUser() {

        return new UserResponse(
                1L,
                "Swathi",
                "swathi@example.com"
        );
    }
}