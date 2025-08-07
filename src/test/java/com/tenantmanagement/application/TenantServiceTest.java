package com.tenantmanagement.application;

import com.tenantmanagement.domain.commands.ActivateTenantCommand;
import com.tenantmanagement.domain.commands.CreateTenantCommand;
import com.tenantmanagement.domain.commands.DeactivateTenantCommand;
import com.tenantmanagement.domain.commands.UpdateTenantCommand;
import com.tenantmanagement.query.TenantView;
import com.tenantmanagement.query.TenantViewRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantServiceTest {

    @Mock
    private CommandGateway commandGateway;

    @Mock
    private TenantViewRepository repository;

    @InjectMocks
    private TenantService tenantService;

    private TenantView testTenant;

    @BeforeEach
    void setUp() {
        testTenant = new TenantView("tenant-123", "Test Tenant", "test.com", "admin@test.com", "ACTIVE");
    }

    @Test
    void testCreateTenant() {
        // Given
        String name = "Test Tenant";
        String domain = "test.com";
        String email = "admin@test.com";
        String expectedTenantId = "tenant-123";
        
        when(commandGateway.send(any(CreateTenantCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(expectedTenantId));

        // When
        CompletableFuture<String> result = tenantService.createTenant(name, domain, email);

        // Then
        assertNotNull(result);
        assertEquals(expectedTenantId, result.join());
        verify(commandGateway).send(any(CreateTenantCommand.class));
    }

    @Test
    void testUpdateTenant() {
        // Given
        String tenantId = "tenant-123";
        String name = "Updated Tenant";
        String domain = "updated.com";
        String email = "admin@updated.com";
        
        when(commandGateway.send(any(UpdateTenantCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        // When
        CompletableFuture<Void> result = tenantService.updateTenant(tenantId, name, domain, email);

        // Then
        assertNotNull(result);
        assertNull(result.join());
        verify(commandGateway).send(any(UpdateTenantCommand.class));
    }

    @Test
    void testActivateTenant() {
        // Given
        String tenantId = "tenant-123";
        
        when(commandGateway.send(any(ActivateTenantCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        // When
        CompletableFuture<Void> result = tenantService.activateTenant(tenantId);

        // Then
        assertNotNull(result);
        assertNull(result.join());
        verify(commandGateway).send(any(ActivateTenantCommand.class));
    }

    @Test
    void testDeactivateTenant() {
        // Given
        String tenantId = "tenant-123";
        
        when(commandGateway.send(any(DeactivateTenantCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        // When
        CompletableFuture<Void> result = tenantService.deactivateTenant(tenantId);

        // Then
        assertNotNull(result);
        assertNull(result.join());
        verify(commandGateway).send(any(DeactivateTenantCommand.class));
    }

    @Test
    void testGetAllTenants() {
        // Given
        List<TenantView> expectedTenants = Arrays.asList(testTenant);
        when(repository.findAll()).thenReturn(expectedTenants);

        // When
        List<TenantView> result = tenantService.getAllTenants();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTenant, result.get(0));
        verify(repository).findAll();
    }

    @Test
    void testGetTenantById() {
        // Given
        String tenantId = "tenant-123";
        when(repository.findById(tenantId)).thenReturn(Optional.of(testTenant));

        // When
        Optional<TenantView> result = tenantService.getTenantById(tenantId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testTenant, result.get());
        verify(repository).findById(tenantId);
    }

    @Test
    void testGetTenantByIdNotFound() {
        // Given
        String tenantId = "non-existent";
        when(repository.findById(tenantId)).thenReturn(Optional.empty());

        // When
        Optional<TenantView> result = tenantService.getTenantById(tenantId);

        // Then
        assertFalse(result.isPresent());
        verify(repository).findById(tenantId);
    }

    @Test
    void testGetTenantByDomain() {
        // Given
        String domain = "test.com";
        when(repository.findByDomain(domain)).thenReturn(Optional.of(testTenant));

        // When
        Optional<TenantView> result = tenantService.getTenantByDomain(domain);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testTenant, result.get());
        verify(repository).findByDomain(domain);
    }

    @Test
    void testGetTenantsByStatus() {
        // Given
        String status = "ACTIVE";
        List<TenantView> expectedTenants = Arrays.asList(testTenant);
        when(repository.findByStatus(status)).thenReturn(expectedTenants);

        // When
        List<TenantView> result = tenantService.getTenantsByStatus(status);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTenant, result.get(0));
        verify(repository).findByStatus(status);
    }

    @Test
    void testSearchTenantsByName() {
        // Given
        String name = "Test";
        List<TenantView> expectedTenants = Arrays.asList(testTenant);
        when(repository.findByNameContainingIgnoreCase(name)).thenReturn(expectedTenants);

        // When
        List<TenantView> result = tenantService.searchTenantsByName(name);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTenant, result.get(0));
        verify(repository).findByNameContainingIgnoreCase(name);
    }
} 