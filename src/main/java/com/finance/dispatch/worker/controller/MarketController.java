package com.finance.dispatch.worker.controller;

import com.finance.dispatch.worker.dto.request.MarketPriceFilter;
import com.finance.dispatch.worker.dto.request.MarketPriceRequest;
import com.finance.dispatch.worker.dto.response.MarketPriceResponse;
import com.finance.dispatch.worker.dto.response.PageResponse;
import com.finance.dispatch.worker.service.MarketPriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/market-price")
public class MarketController {

    private final MarketPriceService marketPriceService;

    //give me the xau spot price in the last n days in json as this format and sort by date as ascending:
    //if it has impact event on USD currency please include in field event:
    //[
    //    {
    //        "date": "01-01-2026",
    //        "opened": 4444.50,
    //        "closed": 4550.50,
    //        "lowest": 4350.984,
    //        "highest": 4568.22,
    //        "event": "FOMC"
    //    },
    //    {
    //        "date": "02-01-2026",
    //        "opened": 4444.50,
    //        "closed": 4550.50,
    //        "lowest": 4350.984,
    //        "highest": 4568.22
    //    }
    //]
    @PostMapping("/insert")
    public void insert(@RequestBody List<MarketPriceRequest> request) {
        marketPriceService.batchInsert(request);
    }

    @PostMapping("/list")
    public ResponseEntity<PageResponse<MarketPriceResponse>> getListPrice(
            @RequestBody MarketPriceFilter marketPriceFilter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(marketPriceService.filterMarketPrice(marketPriceFilter, page, size));
    }

}
