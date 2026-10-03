package com.example.barberbooking.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.barberbooking.dto.AppointmentDto;

import java.util.List;
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

    public List<AppointmentDto> findByUserId(int userId) {

        String sql = """
                SELECT
                    a.appointment_id,
                    a.slot_id,
                    s.service_name,
                    av.slot_date,
                    av.start_time,
                    av.end_time,
                    a.booked_at,
                    a.appointment_status
                FROM appointments a
                JOIN availability_slots av
                    ON a.slot_id = av.slot_id
                JOIN services s
                    ON av.service_id = s.service_id
                WHERE a.user_id = ?
                ORDER BY av.slot_date, av.start_time
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new AppointmentDto(
                        rs.getInt("appointment_id"),
                        rs.getInt("slot_id"),
                        rs.getString("service_name"),
                        rs.getDate("slot_date").toLocalDate(),
                        rs.getTime("start_time").toLocalTime(),
                        rs.getTime("end_time").toLocalTime(),
                        rs.getTimestamp("booked_at").toLocalDateTime(),
                        rs.getString("appointment_status")
                ),
                userId
        );
    }

    public Optional<Integer> findOwnedBookedAppointmentSlot(
            int appointmentId,
            int userId
    ) {
        String sql = """
                SELECT slot_id
                FROM appointments
                WHERE appointment_id = ?
                AND user_id = ?
                AND appointment_status = 'BOOKED'
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getInt("slot_id"),
                appointmentId,
                userId
        ).stream().findFirst();
    }

    public void cancelAppointment(int appointmentId) {
        String sql = """
                UPDATE appointments
                SET appointment_status = 'CANCELLED'
                WHERE appointment_id = ?
                """;

        jdbcTemplate.update(sql, appointmentId);
    }

    public void markSlotAvailable(int slotId) {
        String sql = """
                UPDATE availability_slots
                SET status = 'AVAILABLE'
                WHERE slot_id = ?
                """;

        jdbcTemplate.update(sql, slotId);
    }
}