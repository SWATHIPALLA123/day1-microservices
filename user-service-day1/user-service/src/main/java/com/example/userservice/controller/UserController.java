package com.example.userservice.controller;

import com.example.userservice.dto.UserResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/api/users/{id}")
    public UserResponse getUserById(@PathVariable Long id) {

        return new UserResponse(
                id,
                "User " + id,
                "user" + id + "@example.com"
        );
    }
}