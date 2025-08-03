package com.leopaul29.bento.controllers;

import com.leopaul29.bento.dtos.UserPreferenceDto;
import com.leopaul29.bento.services.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET /users/{id}/preferences
    @GetMapping("/{id}/preferences")
    public UserPreferenceDto getUserPreferences(@PathVariable("id") Long userId) {
        return userService.getPreferencesByUserId(userId);
    }
}
