package com.example.barberbooking.service;

import com.example.barberbooking.dto.SlotDto;
import com.example.barberbooking.repository.SlotRepository;
import org.springframework.stereotype.Service;

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
}