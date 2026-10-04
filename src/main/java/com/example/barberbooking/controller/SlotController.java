package com.example.barberbooking.controller;

import com.example.barberbooking.dto.SlotDto;
import com.example.barberbooking.service.SlotService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/slots")
public class SlotController {

    private final SlotService slotService;

    public SlotController(SlotService slotService) {
        this.slotService = slotService;
    }

    @GetMapping
    public List<SlotDto> getAvailableSlots(
            @RequestParam(required = false) Integer providerId,
            @RequestParam(required = false) Integer serviceId,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return slotService.getAvailableSlots(
                providerId,
                serviceId,
                date,
                page,
                size
        );
    }
}