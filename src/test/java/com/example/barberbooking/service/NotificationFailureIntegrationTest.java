package com.example.barberbooking.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(properties = {
        "notification.base-url=http://127.0.0.1:1",
        "notification.timeout-ms=50",
        "notification.max-attempts=1",
        "notification.retry-delay-ms=0"
})
class NotificationFailureIntegrationTest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void resetDatabase() {
        jdbcTemplate.update("DELETE FROM appointments");
        jdbcTemplate.update(
                "UPDATE availability_slots SET status = 'AVAILABLE'"
        );
    }

    @Test
    void bookingSucceedsWhenNotificationServiceIsDown() {
        assertDoesNotThrow(() -> appointmentService.bookAppointment(3, 1));

        Integer appointmentCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM appointments WHERE user_id = ? AND slot_id = ?",
                Integer.class,
                3,
                1
        );

        String slotStatus = jdbcTemplate.queryForObject(
                "SELECT status FROM availability_slots WHERE slot_id = ?",
                String.class,
                1
        );

        assertEquals(1, appointmentCount);
        assertEquals("BOOKED", slotStatus);
    }
}
