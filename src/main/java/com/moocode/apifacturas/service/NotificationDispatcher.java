package com.moocode.apifacturas.service;
import com.moocode.apifacturas.entity.NotificationOutboxEntity;
import com.moocode.apifacturas.repository.NotificationOutboxRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.moocode.apifacturas.service.EmailNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.TransactionTemplate;

/** Envia avisos después del commit e intenta de nuevo los fallidos. */
@Service
public class NotificationDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(NotificationDispatcher.class);
    private final NotificationOutboxRepository repository;
    private final EmailNotificationService emailService;
    private final int maxAttempts;
    private final long retryDelaySeconds;
    private final TransactionTemplate transactionTemplate;

    public NotificationDispatcher(NotificationOutboxRepository repository, EmailNotificationService emailService,
                                  @Value("${app.notifications.max-attempts:3}") int maxAttempts,
                                  @Value("${app.notifications.retry-delay-seconds:60}") long retryDelaySeconds,
                                  PlatformTransactionManager transactionManager) {
        this.repository = repository;
        this.emailService = emailService;
        this.maxAttempts = maxAttempts;
        this.retryDelaySeconds = retryDelaySeconds;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void dispatch(UUID notificationId) {
        NotificationOutboxEntity notification = repository.findByUuidForUpdate(notificationId).orElse(null);
        if (notification == null || !"PENDIENTE".equals(notification.getDeliveryStatus())
                || notification.getNextAttemptAt().isAfter(Instant.now())) return;

        notification.startAttempt();
        try {
            if ("PAGO".equals(notification.getNotificationType())) {
                emailService.sendPaymentStatus(
                        notification.getRecipient(),
                        notification.getReferenceCode(),
                        notification.getRelatedCode(),
                        notification.getEventStatus(),
                        notification.getAmountText(),
                        notification.getMessageText()
                );
            } else if ("CUENTA".equals(notification.getNotificationType())) {
                emailService.sendAccountActivated(
                        notification.getRecipient(),
                        notification.getMessageText()
                );
            } else {
                emailService.sendInvoiceStatus(
                        notification.getRecipient(),
                        notification.getReferenceCode(),
                        notification.getEventStatus(),
                        notification.getRelatedCode(),
                        notification.getMessageText()
                );
            }
            notification.markSent();
        } catch (RuntimeException exception) {
            boolean finalAttempt = notification.getAttempts() >= maxAttempts;
            long delay = retryDelaySeconds * Math.max(1, notification.getAttempts());
            notification.markFailed(exception.getMessage(), finalAttempt, delay);
            logger.warn("Notificación {} con estado {} tras intento {}: {}", notification.getReferenceCode(),
                    notification.getDeliveryStatus(), notification.getAttempts(), exception.getMessage());
        }
    }

    @Scheduled(fixedDelayString = "${app.notifications.retry-delay-seconds:60}000")
    public void retryDueNotifications() {
        List<UUID> due = repository.findTop50ByDeliveryStatusNextAttemptAtLessThanEqualOrderByCreatedAtAsc(
                        "PENDIENTE", Instant.now()).stream()
                .map(NotificationOutboxEntity::getNotificationUuid).toList();
        due.forEach(id -> transactionTemplate.executeWithoutResult(status -> dispatch(id)));


        }
}