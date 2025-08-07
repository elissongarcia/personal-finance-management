package com.tenantmanagement.domain;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.time.LocalDateTime;
import java.util.UUID;

@Aggregate
public class Tenant {

    @AggregateIdentifier
    private String tenantId;
    private String name;
    private String domain;
    private String email;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor required by Axon
    public Tenant() {}

    @CommandHandler
    public Tenant(CreateTenantCommand command) {
        AggregateLifecycle.apply(new TenantCreatedEvent(
            command.getTenantId(),
            command.getName(),
            command.getDomain(),
            command.getEmail()
        ));
    }

    @CommandHandler
    public void handle(UpdateTenantCommand command) {
        AggregateLifecycle.apply(new TenantUpdatedEvent(
            tenantId,
            command.getName(),
            command.getDomain(),
            command.getEmail()
        ));
    }

    @CommandHandler
    public void handle(ActivateTenantCommand command) {
        if ("ACTIVE".equals(status)) {
            throw new IllegalStateException("Tenant is already active");
        }
        AggregateLifecycle.apply(new TenantActivatedEvent(tenantId));
    }

    @CommandHandler
    public void handle(DeactivateTenantCommand command) {
        if ("INACTIVE".equals(status)) {
            throw new IllegalStateException("Tenant is already inactive");
        }
        AggregateLifecycle.apply(new TenantDeactivatedEvent(tenantId));
    }

    @EventSourcingHandler
    public void on(TenantCreatedEvent event) {
        this.tenantId = event.getTenantId();
        this.name = event.getName();
        this.domain = event.getDomain();
        this.email = event.getEmail();
        this.status = "ACTIVE";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @EventSourcingHandler
    public void on(TenantUpdatedEvent event) {
        this.name = event.getName();
        this.domain = event.getDomain();
        this.email = event.getEmail();
        this.updatedAt = LocalDateTime.now();
    }

    @EventSourcingHandler
    public void on(TenantActivatedEvent event) {
        this.status = "ACTIVE";
        this.updatedAt = LocalDateTime.now();
    }

    @EventSourcingHandler
    public void on(TenantDeactivatedEvent event) {
        this.status = "INACTIVE";
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public String getTenantId() { return tenantId; }
    public String getName() { return name; }
    public String getDomain() { return domain; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
} 