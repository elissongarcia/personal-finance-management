package com.tenantmanagement.domain.events;

import org.axonframework.serialization.Revision;

@Revision("1.0")
public class TenantActivatedEvent {
    
    private final String tenantId;

    public TenantActivatedEvent(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantId() { return tenantId; }
} 