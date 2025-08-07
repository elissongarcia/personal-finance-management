package com.tenantmanagement.query;

import com.tenantmanagement.domain.events.*;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TenantViewEventHandler {

    @Autowired
    private TenantViewRepository repository;

    @EventHandler
    public void on(TenantCreatedEvent event) {
        TenantView tenantView = new TenantView(
            event.getTenantId(),
            event.getName(),
            event.getDomain(),
            event.getEmail(),
            "ACTIVE"
        );
        repository.save(tenantView);
    }

    @EventHandler
    public void on(TenantUpdatedEvent event) {
        repository.findById(event.getTenantId()).ifPresent(tenantView -> {
            tenantView.setName(event.getName());
            tenantView.setDomain(event.getDomain());
            tenantView.setEmail(event.getEmail());
            tenantView.setUpdatedAt(LocalDateTime.now());
            repository.save(tenantView);
        });
    }

    @EventHandler
    public void on(TenantActivatedEvent event) {
        repository.findById(event.getTenantId()).ifPresent(tenantView -> {
            tenantView.setStatus("ACTIVE");
            tenantView.setUpdatedAt(LocalDateTime.now());
            repository.save(tenantView);
        });
    }

    @EventHandler
    public void on(TenantDeactivatedEvent event) {
        repository.findById(event.getTenantId()).ifPresent(tenantView -> {
            tenantView.setStatus("INACTIVE");
            tenantView.setUpdatedAt(LocalDateTime.now());
            repository.save(tenantView);
        });
    }
} 