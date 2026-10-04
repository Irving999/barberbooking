package com.example.barberbooking.controller;

import com.example.barberbooking.dto.CreateSlotRequest;
import com.example.barberbooking.dto.SlotDto;
import com.example.barberbooking.service.SessionService;
import com.example.barberbooking.service.SlotService;
import jakarta.servlet.http.HttpSession;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/provider/slots")
public class ProviderSlotController {

    private final SlotService slotService;
    private final SessionService sessionService;

    public ProviderSlotController(
            SlotService slotService,
            SessionService sessionService
    ) {
        this.slotService = slotService;
        this.sessionService = sessionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createSlot(
            @RequestBody CreateSlotRequest request,
            HttpSession session
    ) {

        int userId = sessionService.requireRole(
                session,
                "PROVIDER"
        );

        slotService.createSlot(
                userId,
                request.serviceId(),
                request.slotDate(),
                request.startTime(),
                request.endTime()
        );
    }

    @DeleteMapping("/{slotId}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void deleteSlot(
                @PathVariable int slotId,
                HttpSession session
        ) {

        int userId = sessionService.requireRole(
                session,
                "PROVIDER"
        );

        slotService.deleteSlot(userId, slotId);
        }
        
    @GetMapping
    public List<SlotDto> getMySlots(HttpSession session) {

        int userId = sessionService.requireRole(
                session,
                "PROVIDER"
        );

        return slotService.getProviderSlots(userId);
    }
}