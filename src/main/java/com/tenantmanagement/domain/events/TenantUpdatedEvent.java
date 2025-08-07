package com.tenantmanagement.domain.events;

import org.axonframework.serialization.Revision;

@Revision("1.0")
public class TenantUpdatedEvent {
    
    private final String tenantId;
    private final String name;
    private final String domain;
    private final String email;

    public TenantUpdatedEvent(String tenantId, String name, String domain, String email) {
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