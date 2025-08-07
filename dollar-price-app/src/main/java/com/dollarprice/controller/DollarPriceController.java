package com.dollarprice.controller;

import com.dollarprice.model.DollarPrice;
import com.dollarprice.repository.DollarPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DollarPriceController {
    
    private final DollarPriceRepository dollarPriceRepository;
    
    @GetMapping("/prices")
    public ResponseEntity<List<DollarPrice>> getPrices() {
        // Get prices from the last 7 days
        Instant sevenDaysAgo = Instant.now().minusSeconds(7 * 24 * 60 * 60);
        List<DollarPrice> prices = dollarPriceRepository.findPricesFromDate(sevenDaysAgo);
        return ResponseEntity.ok(prices);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> getHealth() {
        Map<String, String> healthResponse = new HashMap<>();
        healthResponse.put("status", "UP");
        return ResponseEntity.ok(healthResponse);
    }
} 