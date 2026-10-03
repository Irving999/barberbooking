package com.example.barberbooking.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    public int requireLoggedInUser(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            throw new IllegalStateException("Not logged in");
        }

        return userId;
    }

    public int requireRole(HttpSession session, String requiredRole) {
        Integer userId = (Integer) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        if (userId == null || role == null) {
            throw new IllegalStateException("Not logged in");
        }

        if (!requiredRole.equals(role)) {
            throw new SecurityException("Forbidden");
        }

        return userId;
    }
}