package com.example.barberbooking.repository;

import com.example.barberbooking.dto.ServiceDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ServiceRepository {

    private final JdbcTemplate jdbcTemplate;

    public ServiceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ServiceDto> findAllServices() {

        String sql = """
                SELECT service_id,
                       service_name,
                       duration_minutes,
                       price
                FROM services
                """;

        return jdbcTemplate.query(sql, (resultSet, rowNum) ->
                new ServiceDto(
                        resultSet.getInt("service_id"),
                        resultSet.getString("service_name"),
                        resultSet.getInt("duration_minutes"),
                        resultSet.getBigDecimal("price")
                )
        );
    }
}