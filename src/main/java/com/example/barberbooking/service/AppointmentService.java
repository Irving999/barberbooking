package com.example.barberbooking.service;

import com.example.barberbooking.dto.AppointmentDto;
import com.example.barberbooking.dto.AppointmentNotificationEvent;
import com.example.barberbooking.dto.ProviderAppointmentDto;
import com.example.barberbooking.repository.AppointmentRepository;
import com.example.barberbooking.repository.UserRepository;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            UserRepository userRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
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

        String customerEmail = findCustomerEmail(userId);

        eventPublisher.publishEvent(
                new AppointmentNotificationEvent(
                        customerEmail,
                        "BOOKING_CONFIRMATION",
                        "Your appointment has been booked."
                )
        );
    }

    public List<AppointmentDto> getAppointmentsForUser(int userId) {
        return appointmentRepository.findByUserId(userId);
    }

    @Transactional
    public void cancelAppointment(
            int userId,
            int appointmentId
    ) {
        Integer slotId = appointmentRepository
                .findOwnedBookedAppointmentSlot(
                        appointmentId,
                        userId
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Appointment not found or cannot be cancelled"
                        )
                );

        appointmentRepository.cancelAppointment(
                appointmentId
        );

        appointmentRepository.markSlotAvailable(
                slotId
        );

        String customerEmail = findCustomerEmail(userId);

        eventPublisher.publishEvent(
                new AppointmentNotificationEvent(
                        customerEmail,
                        "CANCELLATION_CONFIRMATION",
                        "Your appointment has been cancelled."
                )
        );
    }

    public List<ProviderAppointmentDto> getAppointmentsForProvider(int userId) {
        return appointmentRepository.findByProviderUserId(userId);
    }

    private String findCustomerEmail(int userId) {
        return userRepository.findEmailById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found"
                ));
    }
}
