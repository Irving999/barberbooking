package com.example.barberbooking.service;

import com.example.barberbooking.dto.NotificationRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class NotificationClient {

    private final RestClient restClient;

    public NotificationClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://localhost:8080")
                .build();
    }

    public void sendNotification(NotificationRequest request) {
        restClient.post()
                .uri("/api/mock-notifications")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}