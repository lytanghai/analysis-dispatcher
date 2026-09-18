package com.finance.dispatch.worker.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApiGoldPriceResponse {

    @JsonProperty("status")
    private Integer status;

    @JsonProperty("body")
    private Body body;

    @Data
    public static class Body {

        @JsonProperty("xau")
        private XAU xau;
    }

    @Data
    public static class XAU {
        @JsonProperty("price")
        private BigDecimal price;
    }

}
