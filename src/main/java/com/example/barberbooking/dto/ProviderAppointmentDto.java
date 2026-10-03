package com.example.barberbooking.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ProviderAppointmentDto(
        int appointmentId,
        int slotId,
        String customerName,
        String customerEmail,
        String serviceName,
        LocalDate slotDate,
        LocalTime startTime,
        LocalTime endTime,
        LocalDateTime bookedAt,
        String appointmentStatus
) {
}