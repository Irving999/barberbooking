package com.example.barberbooking.controller;

import com.example.barberbooking.dto.ServiceDto;
import com.example.barberbooking.service.BarberService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final BarberService barberService;

    public ServiceController(BarberService barberService) {
        this.barberService = barberService;
    }

    @GetMapping
    public List<ServiceDto> getAllServices() {
        return barberService.getAllServices();
    }
}