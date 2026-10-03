package com.example.barberbooking.controller;

import com.example.barberbooking.dto.LoginRequest;
import com.example.barberbooking.dto.LoginResponse;
import com.example.barberbooking.dto.UserDto;
import com.example.barberbooking.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest request,
            HttpSession session
    ) {

        UserDto user = authService.authenticate(
                request.email(),
                request.password()
        );

        session.setAttribute(
                "userId",
                user.userId()
        );

        session.setAttribute(
                "role",
                user.role()
        );

        return new LoginResponse(
                user.userId(),
                user.email(),
                user.role()
        );
    }
}