package com.dollarprice.controller;

import com.dollarprice.model.DollarPrice;
import com.dollarprice.repository.DollarPriceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class DollarPriceControllerIT {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private DollarPriceRepository dollarPriceRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        dollarPriceRepository.deleteAll();
    }
    
    @Test
    void getPrices_ShouldReturnPricesFromLast7Days() throws Exception {
        // Given
        DollarPrice recentPrice = new DollarPrice();
        recentPrice.setPrice(new BigDecimal("1.35"));
        recentPrice.setTimestamp(Instant.now());
        
        DollarPrice oldPrice = new DollarPrice();
        oldPrice.setPrice(new BigDecimal("1.30"));
        oldPrice.setTimestamp(Instant.now().minusSeconds(10 * 24 * 60 * 60)); // 10 days ago
        
        dollarPriceRepository.saveAll(Arrays.asList(recentPrice, oldPrice));
        
        // When & Then
        mockMvc.perform(get("/api/v1/prices")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].price").value("1.35"))
                .andExpect(jsonPath("$[0].timestamp").exists())
                .andExpect(jsonPath("$[1]").doesNotExist()); // Old price should not be included
    }
    
    @Test
    void getPrices_ShouldReturnEmptyArray_WhenNoPrices() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/prices")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
    
    @Test
    void getHealth_ShouldReturnUpStatus() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UP"));
    }
} 