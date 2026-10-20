package com.example.barberbooking.controller;

import com.example.barberbooking.dto.NotificationRequest;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mock-notifications")
public class NotificationController {

    private static final Logger logger =
            LoggerFactory.getLogger(NotificationController.class);

    private final Set<String> deliveredNotificationKeys =
            ConcurrentHashMap.newKeySet();

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void sendNotification(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody NotificationRequest request
    ) {

        if (!deliveredNotificationKeys.add(idempotencyKey)) {
            logger.info(
                    "Duplicate notification ignored: idempotencyKey={}, recipient={}, type={}",
                    idempotencyKey,
                    request.recipient(),
                    request.type()
            );
            return;
        }

        logger.info(
                "Notification sent: recipient={}, type={}, message={}",
                request.recipient(),
                request.type(),
                request.message()
        );
    }
}
