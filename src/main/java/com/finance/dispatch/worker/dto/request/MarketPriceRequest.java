package com.finance.dispatch.worker.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MarketPriceRequest {

    @JsonProperty("date")
    private String date;

    @JsonProperty("opened")
    private BigDecimal opened;

    @JsonProperty("closed")
    private BigDecimal closed;

    @JsonProperty("highest")
    private BigDecimal highest;

    @JsonProperty("lowest")
    private BigDecimal lowest;

    @JsonProperty("event")
    private String event;

}
