package com.example.barberbooking.controller;

import com.example.barberbooking.dto.SlotDto;
import com.example.barberbooking.service.SlotService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/slots")
public class SlotController {

    private final SlotService slotService;

    public SlotController(SlotService slotService) {
        this.slotService = slotService;
    }

    @GetMapping
    public List<SlotDto> getAvailableSlots() {
        return slotService.getAvailableSlots();
    }
}