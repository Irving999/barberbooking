package com.example.barberbooking.service;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

@Component
public class BookingMetrics {

    private final AtomicLong successfulBookings = new AtomicLong();

    public void incrementSuccessfulBookings() {
        successfulBookings.incrementAndGet();
    }

    public long getSuccessfulBookings() {
        return successfulBookings.get();
    }
}
