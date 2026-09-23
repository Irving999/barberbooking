package com.example.barberbooking.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record SlotDto(
        int slotId,
        String barberName,
        String serviceName,
        LocalDate slotDate,
        LocalTime startTime,
        LocalTime endTime
) {
}