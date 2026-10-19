package com.example.barberbooking.dto;

public record AppointmentNotificationEvent(
        String recipient,
        String type,
        String message
) {
}