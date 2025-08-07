package com.dollarprice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "dollar_prices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DollarPrice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Instant timestamp;
} 