package com.finance.dispatch.worker.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoldPriceRequest {

    //OPEN || CLOSE
    @JsonProperty("status")
    private String status;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("symbol")
    private String symbol;

}
