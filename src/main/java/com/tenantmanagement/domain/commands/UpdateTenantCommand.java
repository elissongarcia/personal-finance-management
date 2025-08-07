package com.tenantmanagement.domain.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class UpdateTenantCommand {
    
    @TargetAggregateIdentifier
    private final String tenantId;
    private final String name;
    private final String domain;
    private final String email;

    public UpdateTenantCommand(String tenantId, String name, String domain, String email) {
        this.tenantId = tenantId;
        this.name = name;
        this.domain = domain;
        this.email = email;
    }

    public String getTenantId() { return tenantId; }
    public String getName() { return name; }
    public String getDomain() { return domain; }
    public String getEmail() { return email; }
} 