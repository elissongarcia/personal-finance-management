package com.tenantmanagement.domain.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class ActivateTenantCommand {
    
    @TargetAggregateIdentifier
    private final String tenantId;

    public ActivateTenantCommand(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantId() { return tenantId; }
} 