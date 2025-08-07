package com.tenantmanagement;

import com.tenantmanagement.application.TenantService;
import com.tenantmanagement.query.TenantView;
import com.tenantmanagement.query.TenantViewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class TenantManagementApplicationIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("tenant_management_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TenantService tenantService;

    @Autowired
    private TenantViewRepository repository;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void testCompleteTenantLifecycle() throws Exception {
        // Create tenant
        CompletableFuture<String> createFuture = tenantService.createTenant("Test Tenant", "test.com", "admin@test.com");
        String tenantId = createFuture.get();
        assertNotNull(tenantId);

        // Wait for event sourcing to complete
        Thread.sleep(1000);

        // Verify tenant was created
        Optional<TenantView> createdTenant = tenantService.getTenantById(tenantId);
        assertTrue(createdTenant.isPresent());
        assertEquals("Test Tenant", createdTenant.get().getName());
        assertEquals("test.com", createdTenant.get().getDomain());
        assertEquals("admin@test.com", createdTenant.get().getEmail());
        assertEquals("ACTIVE", createdTenant.get().getStatus());

        // Update tenant
        CompletableFuture<Void> updateFuture = tenantService.updateTenant(tenantId, "Updated Tenant", "updated.com", "admin@updated.com");
        updateFuture.get();

        // Wait for event sourcing to complete
        Thread.sleep(1000);

        // Verify tenant was updated
        Optional<TenantView> updatedTenant = tenantService.getTenantById(tenantId);
        assertTrue(updatedTenant.isPresent());
        assertEquals("Updated Tenant", updatedTenant.get().getName());
        assertEquals("updated.com", updatedTenant.get().getDomain());
        assertEquals("admin@updated.com", updatedTenant.get().getEmail());

        // Deactivate tenant
        CompletableFuture<Void> deactivateFuture = tenantService.deactivateTenant(tenantId);
        deactivateFuture.get();

        // Wait for event sourcing to complete
        Thread.sleep(1000);

        // Verify tenant was deactivated
        Optional<TenantView> deactivatedTenant = tenantService.getTenantById(tenantId);
        assertTrue(deactivatedTenant.isPresent());
        assertEquals("INACTIVE", deactivatedTenant.get().getStatus());

        // Reactivate tenant
        CompletableFuture<Void> activateFuture = tenantService.activateTenant(tenantId);
        activateFuture.get();

        // Wait for event sourcing to complete
        Thread.sleep(1000);

        // Verify tenant was reactivated
        Optional<TenantView> reactivatedTenant = tenantService.getTenantById(tenantId);
        assertTrue(reactivatedTenant.isPresent());
        assertEquals("ACTIVE", reactivatedTenant.get().getStatus());
    }

    @Test
    void testRestApiEndpoints() {
        String baseUrl = "http://localhost:" + port + "/api/tenants";

        // Test create tenant
        TenantController.CreateTenantRequest createRequest = new TenantController.CreateTenantRequest();
        createRequest.setName("API Test Tenant");
        createRequest.setDomain("apitest.com");
        createRequest.setEmail("admin@apitest.com");

        ResponseEntity<String> createResponse = restTemplate.postForEntity(baseUrl, createRequest, String.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());

        String tenantId = createResponse.getBody();

        // Wait for event sourcing to complete
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Test get tenant by ID
        ResponseEntity<TenantView> getResponse = restTemplate.getForEntity(baseUrl + "/" + tenantId, TenantView.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals("API Test Tenant", getResponse.getBody().getName());

        // Test get all tenants
        ResponseEntity<TenantView[]> getAllResponse = restTemplate.getForEntity(baseUrl, TenantView[].class);
        assertEquals(HttpStatus.OK, getAllResponse.getStatusCode());
        assertNotNull(getAllResponse.getBody());
        assertTrue(getAllResponse.getBody().length > 0);

        // Test get tenants by status
        ResponseEntity<TenantView[]> getByStatusResponse = restTemplate.getForEntity(baseUrl + "/status/ACTIVE", TenantView[].class);
        assertEquals(HttpStatus.OK, getByStatusResponse.getStatusCode());
        assertNotNull(getByStatusResponse.getBody());

        // Test search tenants by name
        ResponseEntity<TenantView[]> searchResponse = restTemplate.getForEntity(baseUrl + "/search?name=API", TenantView[].class);
        assertEquals(HttpStatus.OK, searchResponse.getStatusCode());
        assertNotNull(searchResponse.getBody());
        assertTrue(searchResponse.getBody().length > 0);

        // Test update tenant
        TenantController.UpdateTenantRequest updateRequest = new TenantController.UpdateTenantRequest();
        updateRequest.setName("Updated API Tenant");
        updateRequest.setDomain("updatedapitest.com");
        updateRequest.setEmail("admin@updatedapitest.com");

        restTemplate.put(baseUrl + "/" + tenantId, updateRequest);
        
        // Wait for event sourcing to complete
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verify update
        ResponseEntity<TenantView> updatedResponse = restTemplate.getForEntity(baseUrl + "/" + tenantId, TenantView.class);
        assertEquals(HttpStatus.OK, updatedResponse.getStatusCode());
        assertEquals("Updated API Tenant", updatedResponse.getBody().getName());

        // Test deactivate tenant
        ResponseEntity<Void> deactivateResponse = restTemplate.postForEntity(baseUrl + "/" + tenantId + "/deactivate", null, Void.class);
        assertEquals(HttpStatus.OK, deactivateResponse.getStatusCode());

        // Test activate tenant
        ResponseEntity<Void> activateResponse = restTemplate.postForEntity(baseUrl + "/" + tenantId + "/activate", null, Void.class);
        assertEquals(HttpStatus.OK, activateResponse.getStatusCode());
    }

    @Test
    void testQueryMethods() {
        // Create multiple tenants
        tenantService.createTenant("Tenant 1", "tenant1.com", "admin@tenant1.com");
        tenantService.createTenant("Tenant 2", "tenant2.com", "admin@tenant2.com");
        tenantService.createTenant("Tenant 3", "tenant3.com", "admin@tenant3.com");

        // Wait for event sourcing to complete
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Test get all tenants
        List<TenantView> allTenants = tenantService.getAllTenants();
        assertTrue(allTenants.size() >= 3);

        // Test get tenant by domain
        Optional<TenantView> tenantByDomain = tenantService.getTenantByDomain("tenant1.com");
        assertTrue(tenantByDomain.isPresent());
        assertEquals("Tenant 1", tenantByDomain.get().getName());

        // Test get tenants by status
        List<TenantView> activeTenants = tenantService.getTenantsByStatus("ACTIVE");
        assertTrue(activeTenants.size() >= 3);

        // Test search tenants by name
        List<TenantView> searchResults = tenantService.searchTenantsByName("Tenant");
        assertTrue(searchResults.size() >= 3);
    }
} 