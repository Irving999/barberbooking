package com.example.barberbooking.service;

import com.example.barberbooking.dto.NotificationRequest;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class NotificationClient {

    private static final Logger logger =
            LoggerFactory.getLogger(NotificationClient.class);

    private final RestClient restClient;
    private final int maxAttempts;
    private final long retryDelayMs;

    public NotificationClient(
            RestClient.Builder builder,
            @Value("${notification.base-url}") String notificationBaseUrl,
            @Value("${notification.max-attempts}") int maxAttempts,
            @Value("${notification.retry-delay-ms}") long retryDelayMs
    ) {
        this.restClient = builder
                .baseUrl(notificationBaseUrl)
                .build();
        this.maxAttempts = Math.max(1, maxAttempts);
        this.retryDelayMs = Math.max(0, retryDelayMs);
    }

    public void sendNotification(NotificationRequest request) {
        String idempotencyKey = UUID.randomUUID().toString();

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                restClient.post()
                        .uri("/api/mock-notifications")
                        .header("Idempotency-Key", idempotencyKey)
                        .body(request)
                        .retrieve()
                        .toBodilessEntity();

                logger.info(
                        "Notification delivered: recipient={}, type={}, attempt={}",
                        request.recipient(),
                        request.type(),
                        attempt
                );
                return;
            } catch (RestClientException exception) {
                if (attempt == maxAttempts) {
                    logFallback(request, attempt, exception);
                    return;
                }

                logger.warn(
                        "Notification attempt {} of {} failed: recipient={}, type={}. Retrying.",
                        attempt,
                        maxAttempts,
                        request.recipient(),
                        request.type()
                );

                if (!waitBeforeRetry(request, attempt)) {
                    return;
                }
            }
        }
    }

    private boolean waitBeforeRetry(
            NotificationRequest request,
            int attempt
    ) {
        try {
            Thread.sleep(retryDelayMs * attempt);
            return true;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            logFallback(request, attempt, exception);
            return false;
        }
    }

    private void logFallback(
            NotificationRequest request,
            int attempt,
            Exception exception
    ) {
        logger.error(
                "Notification fallback: delivery failed after {} attempt(s); "
                        + "recipient={}, type={}, message={}",
                attempt,
                request.recipient(),
                request.type(),
                request.message(),
                exception
        );
    }
}
