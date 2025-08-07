package com.tenantmanagement.domain;

import com.tenantmanagement.domain.commands.ActivateTenantCommand;
import com.tenantmanagement.domain.commands.CreateTenantCommand;
import com.tenantmanagement.domain.commands.DeactivateTenantCommand;
import com.tenantmanagement.domain.commands.UpdateTenantCommand;
import com.tenantmanagement.domain.events.TenantActivatedEvent;
import com.tenantmanagement.domain.events.TenantCreatedEvent;
import com.tenantmanagement.domain.events.TenantDeactivatedEvent;
import com.tenantmanagement.domain.events.TenantUpdatedEvent;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class TenantTest {

    private FixtureConfiguration<Tenant> fixture;

    @BeforeEach
    void setUp() {
        fixture = new AggregateTestFixture<>(Tenant.class);
    }

    @Test
    void testCreateTenant() {
        String tenantId = "tenant-123";
        String name = "Test Tenant";
        String domain = "test.com";
        String email = "admin@test.com";

        fixture.givenNoPriorActivity()
                .when(new CreateTenantCommand(tenantId, name, domain, email))
                .expectSuccessfulHandlerExecution()
                .expectEvents(new TenantCreatedEvent(tenantId, name, domain, email));
    }

    @Test
    void testUpdateTenant() {
        String tenantId = "tenant-123";
        String name = "Updated Tenant";
        String domain = "updated.com";
        String email = "admin@updated.com";

        fixture.given(new TenantCreatedEvent(tenantId, "Original Tenant", "original.com", "admin@original.com"))
                .when(new UpdateTenantCommand(tenantId, name, domain, email))
                .expectSuccessfulHandlerExecution()
                .expectEvents(new TenantUpdatedEvent(tenantId, name, domain, email));
    }

    @Test
    void testActivateTenant() {
        String tenantId = "tenant-123";

        fixture.given(new TenantCreatedEvent(tenantId, "Test Tenant", "test.com", "admin@test.com"))
                .when(new ActivateTenantCommand(tenantId))
                .expectSuccessfulHandlerExecution()
                .expectEvents(new TenantActivatedEvent(tenantId));
    }

    @Test
    void testActivateAlreadyActiveTenant() {
        String tenantId = "tenant-123";

        fixture.given(new TenantCreatedEvent(tenantId, "Test Tenant", "test.com", "admin@test.com"),
                     new TenantActivatedEvent(tenantId))
                .when(new ActivateTenantCommand(tenantId))
                .expectException(IllegalStateException.class);
    }

    @Test
    void testDeactivateTenant() {
        String tenantId = "tenant-123";

        fixture.given(new TenantCreatedEvent(tenantId, "Test Tenant", "test.com", "admin@test.com"))
                .when(new DeactivateTenantCommand(tenantId))
                .expectSuccessfulHandlerExecution()
                .expectEvents(new TenantDeactivatedEvent(tenantId));
    }

    @Test
    void testDeactivateAlreadyInactiveTenant() {
        String tenantId = "tenant-123";

        fixture.given(new TenantCreatedEvent(tenantId, "Test Tenant", "test.com", "admin@test.com"),
                     new TenantDeactivatedEvent(tenantId))
                .when(new DeactivateTenantCommand(tenantId))
                .expectException(IllegalStateException.class);
    }

    @Test
    void testEventSourcing() {
        String tenantId = "tenant-123";
        String name = "Test Tenant";
        String domain = "test.com";
        String email = "admin@test.com";

        fixture.given(new TenantCreatedEvent(tenantId, name, domain, email))
                .when(new UpdateTenantCommand(tenantId, "Updated Name", "updated.com", "admin@updated.com"))
                .expectSuccessfulHandlerExecution()
                .expectEvents(new TenantUpdatedEvent(tenantId, "Updated Name", "updated.com", "admin@updated.com"));
    }
} 