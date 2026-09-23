package com.finance.dispatch.worker.repository;

import com.finance.dispatch.worker.entity.MarketPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketPriceRepository extends JpaRepository<MarketPrice, Long>, JpaSpecificationExecutor<MarketPrice> {

    @Query(value = """
        SELECT *
        FROM market_price
        ORDER BY id DESC
        LIMIT :days
        """, nativeQuery = true)
    List<MarketPrice> findByDays(@Param("days") Integer days);
}