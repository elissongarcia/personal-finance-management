package com.tenantmanagement.domain.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class DeactivateTenantCommand {
    
    @TargetAggregateIdentifier
    private final String tenantId;

    public DeactivateTenantCommand(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantId() { return tenantId; }
} 