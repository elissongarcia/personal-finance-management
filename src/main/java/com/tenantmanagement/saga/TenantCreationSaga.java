package com.tenantmanagement.saga;

import com.tenantmanagement.domain.events.TenantCreatedEvent;
import com.tenantmanagement.domain.events.TenantActivatedEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
public class TenantCreationSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    private String tenantId;
    private String name;
    private String domain;
    private String email;
    private boolean tenantCreated = false;
    private boolean tenantActivated = false;

    @StartSaga
    @SagaEventHandler(associationProperty = "tenantId")
    public void handle(TenantCreatedEvent event) {
        this.tenantId = event.getTenantId();
        this.name = event.getName();
        this.domain = event.getDomain();
        this.email = event.getEmail();
        this.tenantCreated = true;
        
        // Trigger tenant activation
        commandGateway.send(new com.tenantmanagement.domain.commands.ActivateTenantCommand(tenantId));
    }

    @SagaEventHandler(associationProperty = "tenantId")
    @EndSaga
    public void handle(TenantActivatedEvent event) {
        this.tenantActivated = true;
        // Saga completed successfully
    }

    // Getters for saga state
    public String getTenantId() { return tenantId; }
    public String getName() { return name; }
    public String getDomain() { return domain; }
    public String getEmail() { return email; }
    public boolean isTenantCreated() { return tenantCreated; }
    public boolean isTenantActivated() { return tenantActivated; }
} 