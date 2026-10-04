package com.example.barberbooking.repository;

import com.example.barberbooking.dto.SlotDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Repository
public class SlotRepository {

    private final JdbcTemplate jdbcTemplate;

    public SlotRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SlotDto> findAvailableSlots(
            Integer providerId,
            Integer serviceId,
            LocalDate date,
            int limit,
            int offset
    ) {

        StringBuilder sql = new StringBuilder("""
                SELECT
                    s.slot_id,
                    u.full_name AS barber_name,
                    sv.service_name,
                    s.slot_date,
                    s.start_time,
                    s.end_time
                FROM availability_slots s
                JOIN providers p
                    ON s.provider_id = p.provider_id
                JOIN users u
                    ON p.user_id = u.user_id
                JOIN services sv
                    ON s.service_id = sv.service_id
                WHERE s.status = 'AVAILABLE'
                """);

        List<Object> parameters = new ArrayList<>();

        if (providerId != null) {
            sql.append(" AND s.provider_id = ? ");
            parameters.add(providerId);
        }

        if (serviceId != null) {
            sql.append(" AND s.service_id = ? ");
            parameters.add(serviceId);
        }

        if (date != null) {
            sql.append(" AND s.slot_date = ? ");
            parameters.add(date);
        }

        sql.append("""
                ORDER BY s.slot_date, s.start_time
                LIMIT ? OFFSET ?
                """);

        parameters.add(limit);
        parameters.add(offset);

        return jdbcTemplate.query(
                sql.toString(),
                (resultSet, rowNum) ->
                        new SlotDto(
                                resultSet.getInt("slot_id"),
                                resultSet.getString("barber_name"),
                                resultSet.getString("service_name"),
                                resultSet.getDate("slot_date").toLocalDate(),
                                resultSet.getTime("start_time").toLocalTime(),
                                resultSet.getTime("end_time").toLocalTime()
                        ),
                parameters.toArray()
        );
    }

    public Optional<Integer> findProviderIdByUserId(int userId) {

        String sql = """
                SELECT provider_id
                FROM providers
                WHERE user_id = ?
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getInt("provider_id"),
                userId
        ).stream().findFirst();
    }

    public void createSlot(
            int providerId,
            int serviceId,
            LocalDate slotDate,
            LocalTime startTime,
            LocalTime endTime
    ) {

        String sql = """
                INSERT INTO availability_slots (
                    provider_id,
                    service_id,
                    slot_date,
                    start_time,
                    end_time,
                    status
                )
                VALUES (?, ?, ?, ?, ?, 'AVAILABLE')
                """;

        jdbcTemplate.update(
                sql,
                providerId,
                serviceId,
                slotDate,
                startTime,
                endTime
        );
    }

    public boolean slotBelongsToProvider(int slotId, int providerId) {

        String sql = """
                SELECT COUNT(*)
                FROM availability_slots
                WHERE slot_id = ?
                AND provider_id = ?
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                slotId,
                providerId
        );

        return count != null && count > 0;
    }

    public boolean isSlotBooked(int slotId) {

        String sql = """
                SELECT COUNT(*)
                FROM appointments
                WHERE slot_id = ?
                AND appointment_status = 'BOOKED'
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                slotId
        );

        return count != null && count > 0;
    }

    public void deleteSlot(int slotId, int providerId) {

        String sql = """
                DELETE FROM availability_slots
                WHERE slot_id = ?
                AND provider_id = ?
                """;

        jdbcTemplate.update(
                sql,
                slotId,
                providerId
        );
    }
}
