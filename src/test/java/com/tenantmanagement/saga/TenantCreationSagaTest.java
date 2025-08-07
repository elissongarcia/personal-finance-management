package com.tenantmanagement.saga;

import com.tenantmanagement.domain.commands.ActivateTenantCommand;
import com.tenantmanagement.domain.events.TenantActivatedEvent;
import com.tenantmanagement.domain.events.TenantCreatedEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.test.saga.AnnotatedSagaTestFixture;
import org.axonframework.test.saga.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TenantCreationSagaTest {

    private FixtureConfiguration<TenantCreationSaga> fixture;

    @BeforeEach
    void setUp() {
        fixture = new AnnotatedSagaTestFixture<>(TenantCreationSaga.class);
    }

    @Test
    void testTenantCreationSagaStarts() {
        String tenantId = "tenant-123";
        String name = "Test Tenant";
        String domain = "test.com";
        String email = "admin@test.com";

        fixture.givenNoPriorActivity()
                .whenPublishingA(new TenantCreatedEvent(tenantId, name, domain, email))
                .expectActiveSagas(1)
                .expectDispatchedCommands(new ActivateTenantCommand(tenantId));
    }

    @Test
    void testTenantCreationSagaCompletes() {
        String tenantId = "tenant-123";
        String name = "Test Tenant";
        String domain = "test.com";
        String email = "admin@test.com";

        fixture.givenAPublished(new TenantCreatedEvent(tenantId, name, domain, email))
                .whenPublishingA(new TenantActivatedEvent(tenantId))
                .expectActiveSagas(0)
                .expectNoDispatchedCommands();
    }

    @Test
    void testTenantCreationSagaState() {
        String tenantId = "tenant-123";
        String name = "Test Tenant";
        String domain = "test.com";
        String email = "admin@test.com";

        fixture.givenAPublished(new TenantCreatedEvent(tenantId, name, domain, email))
                .whenPublishingA(new TenantActivatedEvent(tenantId))
                .expectActiveSagas(0);
    }
} 