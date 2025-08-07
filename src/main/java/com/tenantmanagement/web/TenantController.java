package com.tenantmanagement.web;

import com.tenantmanagement.application.TenantService;
import com.tenantmanagement.query.TenantView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/tenants")
@CrossOrigin(origins = "*")
@Tag(name = "Tenant Management", description = "APIs for managing tenants")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @PostMapping
    @Operation(summary = "Create a new tenant", description = "Creates a new tenant with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tenant created successfully", 
                    content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public CompletableFuture<ResponseEntity<String>> createTenant(
            @Parameter(description = "Tenant creation request", required = true)
            @RequestBody CreateTenantRequest request) {
        return tenantService.createTenant(request.getName(), request.getDomain(), request.getEmail())
                .thenApply(tenantId -> ResponseEntity.status(HttpStatus.CREATED).body(tenantId));
    }

    @PutMapping("/{tenantId}")
    @Operation(summary = "Update an existing tenant", description = "Updates the tenant information for the specified tenant ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tenant updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Tenant not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public CompletableFuture<ResponseEntity<Void>> updateTenant(
            @Parameter(description = "Tenant ID", required = true)
            @PathVariable String tenantId,
            @Parameter(description = "Tenant update request", required = true)
            @RequestBody UpdateTenantRequest request) {
        return tenantService.updateTenant(tenantId, request.getName(), request.getDomain(), request.getEmail())
                .thenApply(v -> ResponseEntity.ok().build());
    }

    @PostMapping("/{tenantId}/activate")
    @Operation(summary = "Activate a tenant", description = "Activates the tenant with the specified tenant ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tenant activated successfully"),
        @ApiResponse(responseCode = "404", description = "Tenant not found"),
        @ApiResponse(responseCode = "409", description = "Tenant is already active"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public CompletableFuture<ResponseEntity<Void>> activateTenant(
            @Parameter(description = "Tenant ID", required = true)
            @PathVariable String tenantId) {
        return tenantService.activateTenant(tenantId)
                .thenApply(v -> ResponseEntity.ok().build());
    }

    @PostMapping("/{tenantId}/deactivate")
    @Operation(summary = "Deactivate a tenant", description = "Deactivates the tenant with the specified tenant ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tenant deactivated successfully"),
        @ApiResponse(responseCode = "404", description = "Tenant not found"),
        @ApiResponse(responseCode = "409", description = "Tenant is already inactive"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public CompletableFuture<ResponseEntity<Void>> deactivateTenant(
            @Parameter(description = "Tenant ID", required = true)
            @PathVariable String tenantId) {
        return tenantService.deactivateTenant(tenantId)
                .thenApply(v -> ResponseEntity.ok().build());
    }

    @GetMapping
    @Operation(summary = "Get all tenants", description = "Retrieves a list of all tenants")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tenants retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TenantView.class)))
    })
    public ResponseEntity<List<TenantView>> getAllTenants() {
        return ResponseEntity.ok(tenantService.getAllTenants());
    }

    @GetMapping("/{tenantId}")
    @Operation(summary = "Get tenant by ID", description = "Retrieves a specific tenant by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tenant found",
                    content = @Content(schema = @Schema(implementation = TenantView.class))),
        @ApiResponse(responseCode = "404", description = "Tenant not found")
    })
    public ResponseEntity<TenantView> getTenantById(
            @Parameter(description = "Tenant ID", required = true)
            @PathVariable String tenantId) {
        Optional<TenantView> tenant = tenantService.getTenantById(tenantId);
        return tenant.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/domain/{domain}")
    @Operation(summary = "Get tenant by domain", description = "Retrieves a specific tenant by their domain")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tenant found",
                    content = @Content(schema = @Schema(implementation = TenantView.class))),
        @ApiResponse(responseCode = "404", description = "Tenant not found")
    })
    public ResponseEntity<TenantView> getTenantByDomain(
            @Parameter(description = "Domain name", required = true)
            @PathVariable String domain) {
        Optional<TenantView> tenant = tenantService.getTenantByDomain(domain);
        return tenant.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get tenants by status", description = "Retrieves all tenants with the specified status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tenants retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TenantView.class)))
    })
    public ResponseEntity<List<TenantView>> getTenantsByStatus(
            @Parameter(description = "Tenant status (ACTIVE/INACTIVE)", required = true)
            @PathVariable String status) {
        return ResponseEntity.ok(tenantService.getTenantsByStatus(status));
    }

    @GetMapping("/search")
    @Operation(summary = "Search tenants by name", description = "Searches for tenants whose name contains the specified string")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TenantView.class)))
    })
    public ResponseEntity<List<TenantView>> searchTenantsByName(
            @Parameter(description = "Name to search for", required = true)
            @RequestParam String name) {
        return ResponseEntity.ok(tenantService.searchTenantsByName(name));
    }

    // Request DTOs
    @Schema(description = "Request DTO for creating a tenant")
    public static class CreateTenantRequest {
        @Schema(description = "Tenant name", example = "Acme Corporation")
        private String name;
        
        @Schema(description = "Tenant domain", example = "acme.com")
        private String domain;
        
        @Schema(description = "Tenant email", example = "admin@acme.com")
        private String email;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDomain() { return domain; }
        public void setDomain(String domain) { this.domain = domain; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    @Schema(description = "Request DTO for updating a tenant")
    public static class UpdateTenantRequest {
        @Schema(description = "Tenant name", example = "Acme Corporation")
        private String name;
        
        @Schema(description = "Tenant domain", example = "acme.com")
        private String domain;
        
        @Schema(description = "Tenant email", example = "admin@acme.com")
        private String email;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDomain() { return domain; }
        public void setDomain(String domain) { this.domain = domain; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
} 