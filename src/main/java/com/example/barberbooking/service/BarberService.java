package com.example.barberbooking.service;

import com.example.barberbooking.dto.ServiceDto;
import com.example.barberbooking.repository.ServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BarberService {

    private final ServiceRepository serviceRepository;

    public BarberService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public List<ServiceDto> getAllServices() {
        return serviceRepository.findAllServices();
    }
}