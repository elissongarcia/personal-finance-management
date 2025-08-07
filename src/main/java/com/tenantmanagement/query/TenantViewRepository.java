package com.tenantmanagement.query;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantViewRepository extends JpaRepository<TenantView, String> {
    
    Optional<TenantView> findByDomain(String domain);
    List<TenantView> findByStatus(String status);
    List<TenantView> findByNameContainingIgnoreCase(String name);
} 