package com.tenantmanagement.dto.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.axonframework.serialization.Revision;

import java.time.LocalDateTime;

@Revision("1.0")
public class TenantCreatedEventDTO {
    
    @JsonProperty("tenantId")
    private final String tenantId;
    
    @JsonProperty("name")
    private final String name;
    
    @JsonProperty("domain")
    private final String domain;
    
    @JsonProperty("email")
    private final String email;
    
    @JsonProperty("timestamp")
    private final LocalDateTime timestamp;

    public TenantCreatedEventDTO(String tenantId, String name, String domain, String email) {
        this.tenantId = tenantId;
        this.name = name;
        this.domain = domain;
        this.email = email;
        this.timestamp = LocalDateTime.now();
    }

    public String getTenantId() { 
        return tenantId; 
    }
    
    public String getName() { 
        return name; 
    }
    
    public String getDomain() { 
        return domain; 
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public LocalDateTime getTimestamp() { 
        return timestamp; 
    }
} 