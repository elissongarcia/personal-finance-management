package com.tenantmanagement.saga;

import com.tenantmanagement.domain.events.TenantActivatedEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
public class TenantReactivationSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    private String tenantId;
    private boolean tenantActivated = false;

    @StartSaga
    @SagaEventHandler(associationProperty = "tenantId")
    @EndSaga
    public void handle(TenantActivatedEvent event) {
        this.tenantId = event.getTenantId();
        this.tenantActivated = true;
        
        // Additional reactivation logic can be added here
        // For example, sending notifications, updating external systems, etc.
        
        // Saga completed successfully
    }

    // Getters for saga state
    public String getTenantId() { return tenantId; }
    public boolean isTenantActivated() { return tenantActivated; }
} 