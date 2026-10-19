package com.example.barberbooking.service;

import com.example.barberbooking.dto.AppointmentNotificationEvent;
import com.example.barberbooking.dto.NotificationRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AppointmentNotificationListener {

    private final NotificationClient notificationClient;

    public AppointmentNotificationListener(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAppointmentNotification(AppointmentNotificationEvent event) {
        NotificationRequest request = new NotificationRequest(
                event.recipient(),
                event.type(),
                event.message()
        );

        notificationClient.sendNotification(request);
    }
}
