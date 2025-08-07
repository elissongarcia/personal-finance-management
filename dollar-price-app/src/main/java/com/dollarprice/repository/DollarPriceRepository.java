package com.dollarprice.repository;

import com.dollarprice.model.DollarPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface DollarPriceRepository extends JpaRepository<DollarPrice, Long> {
    
    @Query("SELECT dp FROM DollarPrice dp WHERE dp.timestamp >= :startDate ORDER BY dp.timestamp DESC")
    List<DollarPrice> findPricesFromDate(@Param("startDate") Instant startDate);
    
    @Query("SELECT dp FROM DollarPrice dp WHERE dp.timestamp >= :startDate AND dp.timestamp <= :endDate ORDER BY dp.timestamp DESC")
    List<DollarPrice> findPricesBetweenDates(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
} 