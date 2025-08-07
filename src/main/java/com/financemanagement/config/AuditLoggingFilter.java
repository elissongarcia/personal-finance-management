package com.financemanagement.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLoggingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestId = UUID.randomUUID().toString();
        LocalDateTime startTime = LocalDateTime.now();
        
        // Wrap request and response for content caching
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        
        try {
            // Log request
            logAuditRequest(requestId, wrappedRequest, startTime);
            
            // Process request
            filterChain.doFilter(wrappedRequest, wrappedResponse);
            
            // Log response
            logAuditResponse(requestId, wrappedResponse, startTime);
            
        } finally {
            // Copy response content back to original response
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logAuditRequest(String requestId, ContentCachingRequestWrapper request, LocalDateTime startTime) {
        try {
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String queryString = request.getQueryString();
            String clientIp = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication != null ? authentication.getName() : "anonymous";
            
            String requestBody = "";
            if (request.getContentType() != null && request.getContentType().contains("application/json")) {
                byte[] content = request.getContentAsByteArray();
                if (content.length > 0) {
                    requestBody = new String(content);
                }
            }
            
            log.info("AUDIT_REQUEST | ID: {} | Time: {} | Method: {} | URI: {} | Query: {} | IP: {} | User: {} | User-Agent: {} | Body: {}",
                    requestId,
                    startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    method,
                    uri,
                    queryString != null ? queryString : "",
                    clientIp,
                    username,
                    userAgent != null ? userAgent : "",
                    requestBody);
                    
        } catch (Exception e) {
            log.error("Error logging audit request: {}", e.getMessage(), e);
        }
    }

    private void logAuditResponse(String requestId, ContentCachingResponseWrapper response, LocalDateTime startTime) {
        try {
            LocalDateTime endTime = LocalDateTime.now();
            long duration = java.time.Duration.between(startTime, endTime).toMillis();
            
            int status = response.getStatus();
            String responseBody = "";
            
            if (response.getContentType() != null && response.getContentType().contains("application/json")) {
                byte[] content = response.getContentAsByteArray();
                if (content.length > 0) {
                    responseBody = new String(content);
                }
            }
            
            log.info("AUDIT_RESPONSE | ID: {} | Time: {} | Duration: {}ms | Status: {} | Body: {}",
                    requestId,
                    endTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    duration,
                    status,
                    responseBody);
                    
        } catch (Exception e) {
            log.error("Error logging audit response: {}", e.getMessage(), e);
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Skip audit logging for health checks and static resources
        return path.startsWith("/actuator/health") || 
               path.startsWith("/actuator/info") ||
               path.startsWith("/api-docs") || 
               path.startsWith("/swagger-ui") ||
               path.startsWith("/favicon.ico") ||
               path.startsWith("/error");
    }
} 