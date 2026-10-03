package com.example.barberbooking.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record AppointmentDto(
        int appointmentId,
        int slotId,
        String serviceName,
        LocalDate slotDate,
        LocalTime startTime,
        LocalTime endTime,
        LocalDateTime bookedAt,
        String appointmentStatus
) {
}