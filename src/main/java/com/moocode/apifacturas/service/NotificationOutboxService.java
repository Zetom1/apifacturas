package com.moocode.apifacturas.service;

import com.moocode.apifacturas.entity.NotificationOutboxEntity;
import com.moocode.apifacturas.repository.NotificationOutboxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

@Service
public class NotificationOutboxService {
    private final NotificationOutboxRepository repository;
    private final NotificationDispatcher dispatcher;

    public NotificationOutboxService (NotificationOutboxRepository repository, NotificationDispatcher dispatcher){
        this.repository = repository;
        this.dispatcher = dispatcher;
    }

    public void queuePayment(String recipient, String invoiceCode, String paymentCode,
                             String status, String amount, String comment){
        queue(new NotificationOutboxEntity("PAGO",recipient,paymentCode,status,
                amount, invoiceCode,comment));
    }

    public void queueInvoice(String recipient, String invoiceCode, String status,
                             String stampingFolio, String message) {
        queue(new NotificationOutboxEntity("FACTURA", recipient, invoiceCode, status,
                null, stampingFolio, message));
    }

    public void queueAccountActivation(String recipient, String firstName) {
        queue(new NotificationOutboxEntity("CUENTA", recipient, "ACTIVACION", "ACTIVADA",
                null, null, firstName));
    }

    private void queue(NotificationOutboxEntity notification) {
        UUID id = repository.save(notification).getNotificationUuid();
        if (!TransactionSynchronizationManager.isSynchronizationActive()) return;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() { dispatcher.dispatch(id); }
        });
    }
}
