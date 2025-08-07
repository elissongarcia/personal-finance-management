# Security Documentation

## Overview

This document outlines the comprehensive security measures implemented in the Personal Finance Management Service to protect sensitive financial data and ensure secure operations.

## Security Features

### 1. Authentication & Authorization

#### OAuth2/JWT Implementation
- **JWT Token Authentication**: Secure token-based authentication
- **OAuth2 Resource Server**: Support for OAuth2 authorization
- **Role-Based Access Control (RBAC)**: Fine-grained permissions
- **Token Expiration**: Configurable token lifetime (default: 24 hours)

#### User Roles
- **USER**: Can read and create transactions/accounts
- **ADMIN**: Full access including deletion and statistics

#### Security Headers
```http
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
Strict-Transport-Security: max-age=31536000; includeSubDomains; preload
Content-Security-Policy: default-src 'self'; frame-ancestors 'none'
Referrer-Policy: strict-origin-when-cross-origin
```

### 2. Rate Limiting

#### Implementation
- **Bucket4j**: Token bucket algorithm for rate limiting
- **Per-IP Limiting**: Individual limits per client IP
- **Configurable Limits**: 
  - Default: 100 requests per minute
  - Burst capacity: 200 requests
- **Rate Limit Headers**:
  - `X-Rate-Limit-Remaining`: Remaining requests
  - `X-Rate-Limit-Reset`: Reset time

#### Exemptions
- Health check endpoints
- API documentation
- OAuth2 endpoints

### 3. Input Validation & Sanitization

#### XSS Prevention
- **OWASP Encoder**: HTML encoding for all user inputs
- **Input Sanitization**: Automatic cleaning of malicious content
- **Content Security Policy**: Prevents script injection

#### SQL Injection Prevention
- **Parameterized Queries**: All database queries use prepared statements
- **Input Validation**: Strict validation of all inputs
- **Search Term Sanitization**: Special handling for search queries

#### Validation Rules
```java
// Amount validation
Pattern: ^-?\d+(\.\d{1,2})?$
Range: -999,999.99 to 999,999.99

// UUID validation
Pattern: ^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$

// Date validation
Pattern: ^\d{4}-\d{2}-\d{2}$

// Email validation
Pattern: ^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$
```

### 4. Data Encryption

#### Sensitive Data Encryption
- **AES-256 Encryption**: For sensitive data at rest
- **Account Numbers**: Automatically encrypted
- **Sensitive Notes**: Conditional encryption based on content
- **Secure Key Management**: Environment-based encryption keys

#### Encryption Features
- **Automatic Detection**: Identifies sensitive content
- **Selective Encryption**: Only encrypts when necessary
- **Data Masking**: Logs show masked sensitive data
- **Secure Token Generation**: Cryptographically secure tokens

### 5. Audit Logging

#### Comprehensive Logging
- **Request/Response Logging**: Complete audit trail
- **User Activity Tracking**: All user actions logged
- **Security Events**: Failed authentication, rate limiting
- **Performance Monitoring**: Response times and errors

#### Log Format
```
AUDIT_REQUEST | ID: uuid | Time: timestamp | Method: HTTP_METHOD | 
URI: /api/v1/endpoint | Query: params | IP: client_ip | 
User: username | User-Agent: browser | Body: request_body

AUDIT_RESPONSE | ID: uuid | Time: timestamp | Duration: ms | 
Status: http_status | Body: response_body
```

### 6. CORS Configuration

#### Secure CORS Setup
```yaml
allowed-origins: http://localhost:3000,https://*.yourdomain.com
allowed-methods: GET,POST,PUT,PATCH,DELETE,OPTIONS
allowed-headers: Authorization,Content-Type,X-Requested-With,Accept,Origin
exposed-headers: X-Total-Count,X-Rate-Limit-Remaining
allow-credentials: true
max-age: 3600
```

## Security Configuration

### Environment Variables

#### Required for Production
```bash
# JWT Configuration
JWT_SECRET=your-256-bit-secret-key-here-make-it-long-and-secure

# Encryption Keys
ENCRYPTION_KEY=your-256-bit-encryption-key-here-make-it-long-and-secure
ENCRYPTION_SALT=your-encryption-salt-here-make-it-long-and-secure

# OAuth2 Configuration
OAUTH2_ISSUER_URI=https://your-auth-server.com
OAUTH2_JWK_SET_URI=https://your-auth-server.com/.well-known/jwks.json

# CORS Configuration
CORS_ALLOWED_ORIGINS=https://your-frontend.com,https://admin.yourdomain.com
```

#### Optional Configuration
```bash
# Rate Limiting
SECURITY_RATE_LIMIT_REQUESTS_PER_MINUTE=100
SECURITY_RATE_LIMIT_BURST_CAPACITY=200

# JWT Expiration
JWT_EXPIRATION=86400000
```

### Security Headers

#### HTTP Security Headers
```http
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
Strict-Transport-Security: max-age=31536000; includeSubDomains; preload
Content-Security-Policy: default-src 'self'; frame-ancestors 'none'
Referrer-Policy: strict-origin-when-cross-origin
Permissions-Policy: camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()
```

## Security Testing

### Test Coverage
- **Authentication Tests**: JWT validation, OAuth2 integration
- **Authorization Tests**: Role-based access control
- **Input Validation Tests**: XSS, SQL injection prevention
- **Rate Limiting Tests**: API abuse prevention
- **Encryption Tests**: Data encryption/decryption
- **Security Headers Tests**: HTTP security headers
- **CORS Tests**: Cross-origin resource sharing

### Running Security Tests
```bash
# Run all security tests
./mvnw test -Dtest=SecurityTestSuite

# Run specific security test categories
./mvnw test -Dtest=SecurityTestSuite#shouldValidateInputSanitization
./mvnw test -Dtest=SecurityTestSuite#shouldEncryptAndDecryptData
./mvnw test -Dtest=SecurityTestSuite#shouldApplyRateLimiting
```

## Security Best Practices

### Development
1. **Never commit secrets**: Use environment variables
2. **Validate all inputs**: Sanitize user data
3. **Use HTTPS only**: In production environments
4. **Regular security updates**: Keep dependencies updated
5. **Security code reviews**: Review all security-related changes

### Production Deployment
1. **Strong encryption keys**: Use cryptographically secure keys
2. **Network security**: Use firewalls and VPNs
3. **Monitoring**: Implement security monitoring
4. **Backup security**: Encrypt backups
5. **Access control**: Limit server access

### Data Protection
1. **Encrypt sensitive data**: Account numbers, personal info
2. **Audit logging**: Track all data access
3. **Data retention**: Implement data retention policies
4. **Secure deletion**: Properly delete sensitive data
5. **Access monitoring**: Monitor data access patterns

## Security Incident Response

### Incident Types
1. **Authentication failures**: Monitor for brute force attacks
2. **Rate limit violations**: Potential DoS attacks
3. **Input validation failures**: Potential injection attacks
4. **Encryption errors**: Key management issues
5. **Audit log anomalies**: Unusual access patterns

### Response Procedures
1. **Immediate containment**: Block suspicious IPs
2. **Investigation**: Analyze logs and evidence
3. **Notification**: Alert security team
4. **Remediation**: Fix security issues
5. **Documentation**: Record incident details

## Compliance

### GDPR Compliance
- **Data minimization**: Only collect necessary data
- **Consent management**: User consent for data processing
- **Right to be forgotten**: Data deletion capabilities
- **Data portability**: Export user data
- **Privacy by design**: Built-in privacy protection

### PCI DSS Compliance
- **Data encryption**: Encrypt sensitive financial data
- **Access control**: Restrict access to financial data
- **Audit logging**: Complete audit trail
- **Security monitoring**: Continuous security monitoring
- **Vulnerability management**: Regular security assessments

## Security Monitoring

### Metrics to Monitor
- **Authentication failures**: Failed login attempts
- **Rate limiting events**: API abuse attempts
- **Input validation errors**: Malicious input attempts
- **Encryption errors**: Key management issues
- **Audit log volume**: Unusual activity patterns

### Alerts
- **High failure rates**: Potential attack
- **Rate limit violations**: DoS attack
- **Unusual access patterns**: Security breach
- **Encryption failures**: Key compromise
- **Performance degradation**: Security impact

## Contact Information

For security issues or questions:
- **Security Team**: security@yourdomain.com
- **Bug Reports**: security-bugs@yourdomain.com
- **Emergency**: +1-XXX-XXX-XXXX

---

**Last Updated**: January 2024
**Version**: 1.0
**Review Schedule**: Quarterly 