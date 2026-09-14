package com.finance.dispatch.worker.repository;

import com.finance.dispatch.worker.entity.MarketHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketHistoryRepository extends JpaRepository<MarketHistory, Long> {

    MarketHistory findByDate(String date);
}