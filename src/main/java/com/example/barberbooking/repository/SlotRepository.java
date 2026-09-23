package com.example.barberbooking.repository;

import com.example.barberbooking.dto.SlotDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SlotRepository {

    private final JdbcTemplate jdbcTemplate;

    public SlotRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SlotDto> findAvailableSlots() {

        String sql = """
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
                ORDER BY s.slot_date, s.start_time
                """;

        return jdbcTemplate.query(sql, (resultSet, rowNum) ->
                new SlotDto(
                        resultSet.getInt("slot_id"),
                        resultSet.getString("barber_name"),
                        resultSet.getString("service_name"),
                        resultSet.getDate("slot_date").toLocalDate(),
                        resultSet.getTime("start_time").toLocalTime(),
                        resultSet.getTime("end_time").toLocalTime()
                )
        );
    }
}