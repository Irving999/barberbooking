package com.example.barberbooking.service;

import com.example.barberbooking.repository.AppointmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository
    ) {
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public void bookAppointment(int userId, int slotId) {

        if (slotId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid slot ID"
            );
        }

        Integer lockedSlot = appointmentRepository
                .lockAvailableSlot(slotId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Slot is not available"
                        )
                );

        appointmentRepository.createAppointment(
                userId,
                lockedSlot
        );

        appointmentRepository.markSlotBooked(
                lockedSlot
        );
    }
}