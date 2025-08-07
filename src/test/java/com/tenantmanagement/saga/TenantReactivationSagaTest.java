package com.tenantmanagement.saga;

import com.tenantmanagement.domain.events.TenantActivatedEvent;
import org.axonframework.test.saga.AnnotatedSagaTestFixture;
import org.axonframework.test.saga.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TenantReactivationSagaTest {

    private FixtureConfiguration<TenantReactivationSaga> fixture;

    @BeforeEach
    void setUp() {
        fixture = new AnnotatedSagaTestFixture<>(TenantReactivationSaga.class);
    }

    @Test
    void testTenantReactivationSagaStartsAndCompletes() {
        String tenantId = "tenant-123";

        fixture.givenNoPriorActivity()
                .whenPublishingA(new TenantActivatedEvent(tenantId))
                .expectActiveSagas(0)
                .expectNoDispatchedCommands();
    }

    @Test
    void testTenantReactivationSagaState() {
        String tenantId = "tenant-123";

        fixture.givenAPublished(new TenantActivatedEvent(tenantId))
                .expectActiveSagas(0);
    }
} 