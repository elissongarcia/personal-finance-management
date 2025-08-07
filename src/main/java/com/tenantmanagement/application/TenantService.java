package com.tenantmanagement.application;

import com.tenantmanagement.domain.commands.ActivateTenantCommand;
import com.tenantmanagement.domain.commands.CreateTenantCommand;
import com.tenantmanagement.domain.commands.DeactivateTenantCommand;
import com.tenantmanagement.domain.commands.UpdateTenantCommand;
import com.tenantmanagement.query.TenantView;
import com.tenantmanagement.query.TenantViewRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class TenantService {

    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private TenantViewRepository repository;

    public CompletableFuture<String> createTenant(String name, String domain, String email) {
        String tenantId = UUID.randomUUID().toString();
        CreateTenantCommand command = new CreateTenantCommand(tenantId, name, domain, email);
        return commandGateway.send(command);
    }

    public CompletableFuture<Void> updateTenant(String tenantId, String name, String domain, String email) {
        UpdateTenantCommand command = new UpdateTenantCommand(tenantId, name, domain, email);
        return commandGateway.send(command);
    }

    public CompletableFuture<Void> activateTenant(String tenantId) {
        ActivateTenantCommand command = new ActivateTenantCommand(tenantId);
        return commandGateway.send(command);
    }

    public CompletableFuture<Void> deactivateTenant(String tenantId) {
        DeactivateTenantCommand command = new DeactivateTenantCommand(tenantId);
        return commandGateway.send(command);
    }

    // Query methods
    public List<TenantView> getAllTenants() {
        return repository.findAll();
    }

    public Optional<TenantView> getTenantById(String tenantId) {
        return repository.findById(tenantId);
    }

    public Optional<TenantView> getTenantByDomain(String domain) {
        return repository.findByDomain(domain);
    }

    public List<TenantView> getTenantsByStatus(String status) {
        return repository.findByStatus(status);
    }

    public List<TenantView> searchTenantsByName(String name) {
        return repository.findByNameContainingIgnoreCase(name);
    }
} 