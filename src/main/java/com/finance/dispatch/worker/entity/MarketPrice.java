package com.finance.dispatch.worker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "market_price")
public class MarketPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "date")
    private String date;

    @Column(name = "opened")
    private BigDecimal opened;

    @Column(name = "closed")
    private BigDecimal closed;

    @Column(name = "highest")
    private BigDecimal highest;

    @Column(name = "lowest")
    private BigDecimal lowest;

    @Column(name = "changed")
    private BigDecimal changed;

    @Column(name = "event")
    private String event;
}
