package com.example.userservice.controller;

import org.springframework.http.ResponseEntity; 
import org.springframework.web.bind.annotation.*;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.service.UserService;
import com.example.userservice.service.UserNotFoundException;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}
