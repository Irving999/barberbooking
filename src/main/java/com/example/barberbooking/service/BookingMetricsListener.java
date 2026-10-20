package com.example.barberbooking.service;

import com.example.barberbooking.dto.AppointmentNotificationEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class BookingMetricsListener {

    private final BookingMetrics bookingMetrics;

    public BookingMetricsListener(BookingMetrics bookingMetrics) {
        this.bookingMetrics = bookingMetrics;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void countSuccessfulBooking(AppointmentNotificationEvent event) {
        if ("BOOKING_CONFIRMATION".equals(event.type())) {
            bookingMetrics.incrementSuccessfulBookings();
        }
    }
}
