package com.example.barberbooking.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateSlotRequest(
        int serviceId,
        LocalDate slotDate,
        LocalTime startTime,
        LocalTime endTime
) {
}