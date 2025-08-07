package com.dollarprice.service;

import com.dollarprice.model.DollarPrice;
import com.dollarprice.repository.DollarPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {
    
    private final DollarPriceRepository dollarPriceRepository;
    private final RestTemplate restTemplate;
    
    private static final String API_URL = "https://api.frankfurter.app/latest?from=USD&to=CAD";
    
    @Scheduled(cron = "0 0 0 * * ?") // Run at midnight every day
    public void fetchAndSaveDollarPrice() {
        try {
            log.info("Fetching dollar price from API...");
            
            // Call the API
            Map<String, Object> response = restTemplate.getForObject(API_URL, Map.class);
            
            if (response != null && response.containsKey("rates")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> rates = (Map<String, Object>) response.get("rates");
                
                if (rates.containsKey("CAD")) {
                    BigDecimal price = new BigDecimal(rates.get("CAD").toString());
                    Instant timestamp = Instant.now();
                    
                    DollarPrice dollarPrice = new DollarPrice();
                    dollarPrice.setPrice(price);
                    dollarPrice.setTimestamp(timestamp);
                    
                    dollarPriceRepository.save(dollarPrice);
                    log.info("Saved dollar price: {} CAD at {}", price, timestamp);
                } else {
                    log.error("CAD rate not found in API response");
                }
            } else {
                log.error("Invalid API response format");
            }
        } catch (Exception e) {
            log.error("Error fetching dollar price: {}", e.getMessage(), e);
        }
    }
} 