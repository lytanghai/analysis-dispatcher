package com.finance.dispatch.worker.dto.response;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class MarketNews {

    private String title;

    private String country;

    private OffsetDateTime date;

    private String impact;

    private String forecast;

    private String previous;
}