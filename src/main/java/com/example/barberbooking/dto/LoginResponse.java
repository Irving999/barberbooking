package com.example.barberbooking.dto;

public record LoginResponse(
        int userId,
        String email,
        String role
) {
}