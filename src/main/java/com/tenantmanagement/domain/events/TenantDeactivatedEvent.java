package com.tenantmanagement.domain.events;

import org.axonframework.serialization.Revision;

@Revision("1.0")
public class TenantDeactivatedEvent {
    
    private final String tenantId;

    public TenantDeactivatedEvent(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantId() { return tenantId; }
} 