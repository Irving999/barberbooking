package com.example.barberbooking.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AppointmentRepository {

    private final JdbcTemplate jdbcTemplate;

    public AppointmentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Integer> lockAvailableSlot(int slotId) {

        String sql = """
                SELECT slot_id
                FROM availability_slots
                WHERE slot_id = ?
                  AND status = 'AVAILABLE'
                FOR UPDATE
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getInt("slot_id"),
                slotId
        ).stream().findFirst();
    }

    public void createAppointment(int userId, int slotId) {

        String sql = """
                INSERT INTO appointments (
                    user_id,
                    slot_id,
                    booked_at,
                    appointment_status
                )
                VALUES (?, ?, CURRENT_TIMESTAMP, 'BOOKED')
                """;

        jdbcTemplate.update(
                sql,
                userId,
                slotId
        );
    }

    public void markSlotBooked(int slotId) {

        String sql = """
                UPDATE availability_slots
                SET status = 'BOOKED'
                WHERE slot_id = ?
                """;

        jdbcTemplate.update(
                sql,
                slotId
        );
    }
}