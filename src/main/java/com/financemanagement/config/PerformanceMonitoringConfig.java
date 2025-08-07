package com.financemanagement.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableAspectJAutoProxy
@Slf4j
public class PerformanceMonitoringConfig {

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    @Bean
    public Timer transactionCreationTimer(MeterRegistry registry) {
        return Timer.builder("finance.transaction.creation")
                .description("Time taken to create a transaction")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .register(registry);
    }

    @Bean
    public Timer transactionQueryTimer(MeterRegistry registry) {
        return Timer.builder("finance.transaction.query")
                .description("Time taken to query transactions")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .register(registry);
    }

    @Bean
    public Timer reportGenerationTimer(MeterRegistry registry) {
        return Timer.builder("finance.report.generation")
                .description("Time taken to generate financial reports")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .register(registry);
    }

    @Bean
    public Timer cacheHitTimer(MeterRegistry registry) {
        return Timer.builder("finance.cache.hit")
                .description("Cache hit response time")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
    }

    @Bean
    public Timer databaseQueryTimer(MeterRegistry registry) {
        return Timer.builder("finance.database.query")
                .description("Database query execution time")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .register(registry);
    }

    @Bean
    public Timer asyncTaskTimer(MeterRegistry registry) {
        return Timer.builder("finance.async.task")
                .description("Async task execution time")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .register(registry);
    }

    @Bean
    public Timer encryptionTimer(MeterRegistry registry) {
        return Timer.builder("finance.encryption")
                .description("Data encryption/decryption time")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
    }

    @Bean
    public Timer validationTimer(MeterRegistry registry) {
        return Timer.builder("finance.validation")
                .description("Input validation time")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
    }

    @Bean
    public Timer rateLimitTimer(MeterRegistry registry) {
        return Timer.builder("finance.rate.limit")
                .description("Rate limiting check time")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
    }

    @Bean
    public Timer auditLogTimer(MeterRegistry registry) {
        return Timer.builder("finance.audit.log")
                .description("Audit logging time")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
    }

    @Bean
    public Timer redisOperationTimer(MeterRegistry registry) {
        return Timer.builder("finance.redis.operation")
                .description("Redis operation time")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .register(registry);
    }

    @Bean
    public Timer axonCommandTimer(MeterRegistry registry) {
        return Timer.builder("finance.axon.command")
                .description("Axon command processing time")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .register(registry);
    }

    @Bean
    public Timer axonEventTimer(MeterRegistry registry) {
        return Timer.builder("finance.axon.event")
                .description("Axon event processing time")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .register(registry);
    }

    @Bean
    public Timer jvmGcTimer(MeterRegistry registry) {
        return Timer.builder("finance.jvm.gc")
                .description("JVM garbage collection time")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
    }

    @Bean
    public Timer memoryUsageTimer(MeterRegistry registry) {
        return Timer.builder("finance.memory.usage")
                .description("Memory usage monitoring")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
    }

    @Bean
    public Timer threadPoolTimer(MeterRegistry registry) {
        return Timer.builder("finance.thread.pool")
                .description("Thread pool utilization")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
    }

    @Bean
    public Timer connectionPoolTimer(MeterRegistry registry) {
        return Timer.builder("finance.connection.pool")
                .description("Database connection pool metrics")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
    }
} 