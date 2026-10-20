package com.example.barberbooking.service;

import com.example.barberbooking.repository.AppointmentRepository;
import com.example.barberbooking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {
        appointmentService =
                new AppointmentService(
                        appointmentRepository,
                        userRepository,
                        eventPublisher
                );
    }

    @Test
    void bookAvailableSlotSucceeds() {

        when(appointmentRepository.lockAvailableSlot(1))
                .thenReturn(Optional.of(1));
        when(userRepository.findEmailById(3))
                .thenReturn(Optional.of("john@example.com"));

        appointmentService.bookAppointment(3, 1);

        verify(appointmentRepository)
                .createAppointment(3, 1);

        verify(appointmentRepository)
                .markSlotBooked(1);

        verify(eventPublisher).publishEvent(
                new com.example.barberbooking.dto.AppointmentNotificationEvent(
                        "john@example.com",
                        "BOOKING_CONFIRMATION",
                        "Your appointment has been booked."
                )
        );
    }

    @Test
    void bookUnavailableSlotReturns409() {

        when(appointmentRepository.lockAvailableSlot(1))
                .thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> appointmentService.bookAppointment(3, 1)
                );

        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatusCode()
        );

        verify(appointmentRepository, never())
                .createAppointment(anyInt(), anyInt());

        verify(appointmentRepository, never())
                .markSlotBooked(anyInt());
    }

    @Test
    void invalidSlotIdReturns400() {

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> appointmentService.bookAppointment(3, 0)
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void ownerCanCancelAppointment() {

        when(
                appointmentRepository
                        .findOwnedBookedAppointmentSlot(10, 3)
        ).thenReturn(Optional.of(1));
        when(userRepository.findEmailById(3))
                .thenReturn(Optional.of("john@example.com"));

        appointmentService.cancelAppointment(3, 10);

        verify(appointmentRepository)
                .cancelAppointment(10);

        verify(appointmentRepository)
                .markSlotAvailable(1);

        verify(eventPublisher).publishEvent(
                new com.example.barberbooking.dto.AppointmentNotificationEvent(
                        "john@example.com",
                        "CANCELLATION_CONFIRMATION",
                        "Your appointment has been cancelled."
                )
        );
    }

    @Test
    void nonOwnerCannotCancelAppointment() {

        when(
                appointmentRepository
                        .findOwnedBookedAppointmentSlot(10, 4)
        ).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> appointmentService.cancelAppointment(4, 10)
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );

        verify(appointmentRepository, never())
                .cancelAppointment(anyInt());

        verify(appointmentRepository, never())
                .markSlotAvailable(anyInt());
    }
}
