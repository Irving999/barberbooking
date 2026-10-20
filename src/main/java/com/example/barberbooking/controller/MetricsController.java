package com.example.barberbooking.controller;

import com.example.barberbooking.dto.MetricsResponse;
import com.example.barberbooking.service.BookingMetrics;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetricsController {

    private final BookingMetrics bookingMetrics;

    public MetricsController(BookingMetrics bookingMetrics) {
        this.bookingMetrics = bookingMetrics;
    }

    @GetMapping("/metrics")
    public MetricsResponse metrics() {
        return new MetricsResponse(
                bookingMetrics.getSuccessfulBookings()
        );
    }
}
