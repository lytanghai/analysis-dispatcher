package com.finance.dispatch.worker.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Data
public class MarketPriceResponse {

    @JsonProperty("id")
    private Long id;

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

    @JsonProperty("changed")
    private BigDecimal changed;

    @JsonProperty("event")
    private String event;

}
