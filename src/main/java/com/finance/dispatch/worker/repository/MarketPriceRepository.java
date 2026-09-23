package com.finance.dispatch.worker.repository;

import com.finance.dispatch.worker.entity.MarketPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketPriceRepository extends JpaRepository<MarketPrice, Long>, JpaSpecificationExecutor<MarketPrice> {

}