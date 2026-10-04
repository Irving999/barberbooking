package com.example.barberbooking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BookingConcurrencyTest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void resetDatabase() {

        jdbcTemplate.update(
                "DELETE FROM appointments WHERE slot_id = ?",
                1
        );

        jdbcTemplate.update(
                """
                UPDATE availability_slots
                SET status = 'AVAILABLE'
                WHERE slot_id = ?
                """,
                1
        );
    }

    @Test
    void onlyOneCustomerCanBookSameSlot()
            throws Exception {

        int slotId = 1;

        int johnUserId = 3;
        int janeUserId = 4;

        ExecutorService executor =
                Executors.newFixedThreadPool(2);

        CountDownLatch ready =
                new CountDownLatch(2);

        CountDownLatch start =
                new CountDownLatch(1);

        Callable<Boolean> johnBooking = () -> {

            ready.countDown();
            start.await();

            try {
                appointmentService.bookAppointment(
                        johnUserId,
                        slotId
                );

                return true;

            } catch (ResponseStatusException ex) {

                if (ex.getStatusCode()
                        == HttpStatus.CONFLICT) {
                    return false;
                }

                throw ex;
            }
        };

        Callable<Boolean> janeBooking = () -> {

            ready.countDown();
            start.await();

            try {
                appointmentService.bookAppointment(
                        janeUserId,
                        slotId
                );

                return true;

            } catch (ResponseStatusException ex) {

                if (ex.getStatusCode()
                        == HttpStatus.CONFLICT) {
                    return false;
                }

                throw ex;
            }
        };

        Future<Boolean> johnResult =
                executor.submit(johnBooking);

        Future<Boolean> janeResult =
                executor.submit(janeBooking);

        ready.await();

        start.countDown();

        boolean johnSucceeded =
                johnResult.get();

        boolean janeSucceeded =
                janeResult.get();

        executor.shutdown();

        int successCount = 0;

        if (johnSucceeded) {
            successCount++;
        }

        if (janeSucceeded) {
            successCount++;
        }

        assertEquals(
                1,
                successCount
        );

        Integer appointmentCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM appointments
                        WHERE slot_id = ?
                        """,
                        Integer.class,
                        slotId
                );

        assertEquals(
                1,
                appointmentCount
        );

        String slotStatus =
                jdbcTemplate.queryForObject(
                        """
                        SELECT status
                        FROM availability_slots
                        WHERE slot_id = ?
                        """,
                        String.class,
                        slotId
                );

        assertEquals(
                "BOOKED",
                slotStatus
        );
    }
}