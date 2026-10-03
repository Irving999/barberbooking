package com.example.barberbooking.dto;

public record UserDto(
        int userId,
        String email,
        String passwordHash,
        String role
) {
}