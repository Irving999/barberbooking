package com.example.barberbooking.dto;

import java.math.BigDecimal;

public record ServiceDto(
        int serviceId,
        String serviceName,
        int durationMinutes,
        BigDecimal price
) {
}
