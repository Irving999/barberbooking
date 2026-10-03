package com.example.barberbooking.controller;

import com.example.barberbooking.dto.BookingRequest;
import com.example.barberbooking.service.AppointmentService;
import com.example.barberbooking.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final SessionService sessionService;

    public AppointmentController(
            AppointmentService appointmentService,
            SessionService sessionService
    ) {
        this.appointmentService = appointmentService;
        this.sessionService = sessionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void bookAppointment(
            @RequestBody BookingRequest request,
            HttpSession session
    ) {
        int userId = sessionService.requireRole(
                session,
                "CUSTOMER"
        );

        appointmentService.bookAppointment(
                userId,
                request.slotId()
        );
    }
}