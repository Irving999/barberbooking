package com.example.barberbooking.dto;

public record NotificationRequest(
        String recipient,
        String type,
        String message
) {
}