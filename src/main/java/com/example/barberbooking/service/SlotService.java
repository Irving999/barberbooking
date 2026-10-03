package com.example.barberbooking.service;

import com.example.barberbooking.dto.SlotDto;
import com.example.barberbooking.repository.SlotRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class SlotService {

    private final SlotRepository slotRepository;

    public SlotService(SlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    public List<SlotDto> getAvailableSlots() {
        return slotRepository.findAvailableSlots();
    }

    public void createSlot(
            int userId,
            int serviceId,
            LocalDate slotDate,
            LocalTime startTime,
            LocalTime endTime
    ) {

        if (serviceId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid service ID"
            );
        }

        if (slotDate == null || startTime == null || endTime == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Date and times are required"
            );
        }

        if (!endTime.isAfter(startTime)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "End time must be after start time"
            );
        }

        int providerId = slotRepository
                .findProviderIdByUserId(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Provider not found"
                        )
                );

        slotRepository.createSlot(
                providerId,
                serviceId,
                slotDate,
                startTime,
                endTime
        );
    }

    public void deleteSlot(int userId, int slotId) {

        if (slotId <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid slot ID"
                );
        }

        int providerId = slotRepository
                .findProviderIdByUserId(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Provider not found"
                        )
                );

        boolean exists = slotRepository
                .slotBelongsToProvider(slotId, providerId);

        if (!exists) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Slot not found"
                );
        }

        if (slotRepository.isSlotBooked(slotId)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Cannot remove a booked slot"
                );
        }

        slotRepository.deleteSlot(slotId, providerId);
        }
}