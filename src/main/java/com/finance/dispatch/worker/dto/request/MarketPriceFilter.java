package com.finance.dispatch.worker.dto.request;

import lombok.Data;

@Data
public class MarketPriceFilter {

    private Long id;

    private String date;

    private String event;

}
