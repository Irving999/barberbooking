package com.example.barberbooking.controller;

import com.example.barberbooking.dto.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mock-notifications")
public class NotificationController {

    private static final Logger logger =
            LoggerFactory.getLogger(NotificationController.class);

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void sendNotification(
            @RequestBody NotificationRequest request
    ) {

        logger.info(
                "Notification sent: recipient={}, type={}, message={}",
                request.recipient(),
                request.type(),
                request.message()
        );
    }
}