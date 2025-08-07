package com.dollarprice.service;

import com.dollarprice.model.DollarPrice;
import com.dollarprice.repository.DollarPriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {
    
    @Mock
    private DollarPriceRepository dollarPriceRepository;
    
    @Mock
    private RestTemplate restTemplate;
    
    @InjectMocks
    private CurrencyService currencyService;
    
    private Map<String, Object> mockApiResponse;
    
    @BeforeEach
    void setUp() {
        mockApiResponse = new HashMap<>();
        Map<String, Object> rates = new HashMap<>();
        rates.put("CAD", 1.35);
        mockApiResponse.put("rates", rates);
    }
    
    @Test
    void fetchAndSaveDollarPrice_Success() {
        // Given
        when(restTemplate.getForObject(any(String.class), eq(Map.class)))
                .thenReturn(mockApiResponse);
        when(dollarPriceRepository.save(any(DollarPrice.class)))
                .thenReturn(new DollarPrice());
        
        // When
        currencyService.fetchAndSaveDollarPrice();
        
        // Then
        verify(restTemplate).getForObject(any(String.class), eq(Map.class));
        verify(dollarPriceRepository).save(any(DollarPrice.class));
    }
    
    @Test
    void fetchAndSaveDollarPrice_ApiError() {
        // Given
        when(restTemplate.getForObject(any(String.class), eq(Map.class)))
                .thenThrow(new RuntimeException("API Error"));
        
        // When
        currencyService.fetchAndSaveDollarPrice();
        
        // Then
        verify(restTemplate).getForObject(any(String.class), eq(Map.class));
        verify(dollarPriceRepository, never()).save(any(DollarPrice.class));
    }
    
    @Test
    void fetchAndSaveDollarPrice_InvalidResponse() {
        // Given
        Map<String, Object> invalidResponse = new HashMap<>();
        when(restTemplate.getForObject(any(String.class), eq(Map.class)))
                .thenReturn(invalidResponse);
        
        // When
        currencyService.fetchAndSaveDollarPrice();
        
        // Then
        verify(restTemplate).getForObject(any(String.class), eq(Map.class));
        verify(dollarPriceRepository, never()).save(any(DollarPrice.class));
    }
    
    @Test
    void fetchAndSaveDollarPrice_CadRateNotFound() {
        // Given
        Map<String, Object> responseWithoutCad = new HashMap<>();
        Map<String, Object> rates = new HashMap<>();
        rates.put("EUR", 0.85);
        responseWithoutCad.put("rates", rates);
        
        when(restTemplate.getForObject(any(String.class), eq(Map.class)))
                .thenReturn(responseWithoutCad);
        
        // When
        currencyService.fetchAndSaveDollarPrice();
        
        // Then
        verify(restTemplate).getForObject(any(String.class), eq(Map.class));
        verify(dollarPriceRepository, never()).save(any(DollarPrice.class));
    }
} 