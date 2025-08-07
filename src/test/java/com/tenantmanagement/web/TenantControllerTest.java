package com.tenantmanagement.web;

import com.tenantmanagement.application.TenantService;
import com.tenantmanagement.query.TenantView;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TenantController.class)
class TenantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TenantService tenantService;

    @Autowired
    private ObjectMapper objectMapper;

    private TenantView testTenant;

    @BeforeEach
    void setUp() {
        testTenant = new TenantView("tenant-123", "Test Tenant", "test.com", "admin@test.com", "ACTIVE");
    }

    @Test
    void testCreateTenant() throws Exception {
        // Given
        TenantController.CreateTenantRequest request = new TenantController.CreateTenantRequest();
        request.setName("Test Tenant");
        request.setDomain("test.com");
        request.setEmail("admin@test.com");
        
        when(tenantService.createTenant(anyString(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture("tenant-123"));

        // When & Then
        mockMvc.perform(post("/api/tenants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("tenant-123"));
    }

    @Test
    void testUpdateTenant() throws Exception {
        // Given
        TenantController.UpdateTenantRequest request = new TenantController.UpdateTenantRequest();
        request.setName("Updated Tenant");
        request.setDomain("updated.com");
        request.setEmail("admin@updated.com");
        
        when(tenantService.updateTenant(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));

        // When & Then
        mockMvc.perform(put("/api/tenants/tenant-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testActivateTenant() throws Exception {
        // Given
        when(tenantService.activateTenant(anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));

        // When & Then
        mockMvc.perform(post("/api/tenants/tenant-123/activate"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeactivateTenant() throws Exception {
        // Given
        when(tenantService.deactivateTenant(anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));

        // When & Then
        mockMvc.perform(post("/api/tenants/tenant-123/deactivate"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllTenants() throws Exception {
        // Given
        List<TenantView> tenants = Arrays.asList(testTenant);
        when(tenantService.getAllTenants()).thenReturn(tenants);

        // When & Then
        mockMvc.perform(get("/api/tenants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tenantId").value("tenant-123"))
                .andExpect(jsonPath("$[0].name").value("Test Tenant"))
                .andExpect(jsonPath("$[0].domain").value("test.com"))
                .andExpect(jsonPath("$[0].email").value("admin@test.com"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    @Test
    void testGetTenantById() throws Exception {
        // Given
        when(tenantService.getTenantById("tenant-123")).thenReturn(Optional.of(testTenant));

        // When & Then
        mockMvc.perform(get("/api/tenants/tenant-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantId").value("tenant-123"))
                .andExpect(jsonPath("$.name").value("Test Tenant"))
                .andExpect(jsonPath("$.domain").value("test.com"))
                .andExpect(jsonPath("$.email").value("admin@test.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void testGetTenantByIdNotFound() throws Exception {
        // Given
        when(tenantService.getTenantById("non-existent")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/tenants/non-existent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetTenantByDomain() throws Exception {
        // Given
        when(tenantService.getTenantByDomain("test.com")).thenReturn(Optional.of(testTenant));

        // When & Then
        mockMvc.perform(get("/api/tenants/domain/test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantId").value("tenant-123"))
                .andExpect(jsonPath("$.name").value("Test Tenant"))
                .andExpect(jsonPath("$.domain").value("test.com"))
                .andExpect(jsonPath("$.email").value("admin@test.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void testGetTenantByDomainNotFound() throws Exception {
        // Given
        when(tenantService.getTenantByDomain("non-existent.com")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/tenants/domain/non-existent.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetTenantsByStatus() throws Exception {
        // Given
        List<TenantView> tenants = Arrays.asList(testTenant);
        when(tenantService.getTenantsByStatus("ACTIVE")).thenReturn(tenants);

        // When & Then
        mockMvc.perform(get("/api/tenants/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tenantId").value("tenant-123"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    @Test
    void testSearchTenantsByName() throws Exception {
        // Given
        List<TenantView> tenants = Arrays.asList(testTenant);
        when(tenantService.searchTenantsByName("Test")).thenReturn(tenants);

        // When & Then
        mockMvc.perform(get("/api/tenants/search")
                .param("name", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tenantId").value("tenant-123"))
                .andExpect(jsonPath("$[0].name").value("Test Tenant"));
    }
} 