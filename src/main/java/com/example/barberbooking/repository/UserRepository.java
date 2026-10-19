package com.example.barberbooking.repository;

import com.example.barberbooking.dto.UserDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<UserDto> findByEmail(String email) {

        String sql = """
                SELECT
                    user_id,
                    email,
                    password_hash,
                    role
                FROM users
                WHERE email = ?
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new UserDto(
                        rs.getInt("user_id"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        rs.getString("role")
                ),
                email
        ).stream().findFirst();
    }

    public Optional<String> findEmailById(int userId) {

        String sql = """
                SELECT email
                FROM users
                WHERE user_id = ?
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("email"),
                userId
        ).stream().findFirst();
    }
}
