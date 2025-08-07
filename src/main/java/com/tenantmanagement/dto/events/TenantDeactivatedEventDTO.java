package com.tenantmanagement.dto.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.axonframework.serialization.Revision;

import java.time.LocalDateTime;

@Revision("1.0")
public class TenantDeactivatedEventDTO {
    
    @JsonProperty("tenantId")
    private final String tenantId;
    
    @JsonProperty("timestamp")
    private final LocalDateTime timestamp;

    public TenantDeactivatedEventDTO(String tenantId) {
        this.tenantId = tenantId;
        this.timestamp = LocalDateTime.now();
    }

    public String getTenantId() { 
        return tenantId; 
    }
    
    public LocalDateTime getTimestamp() { 
        return timestamp; 
    }
} 