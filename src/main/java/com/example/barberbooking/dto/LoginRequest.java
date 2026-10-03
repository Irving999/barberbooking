package com.example.barberbooking.dto;

public record LoginRequest(
        String email,
        String password
) {
}