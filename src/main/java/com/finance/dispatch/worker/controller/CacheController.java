package com.finance.dispatch.worker.controller;

import com.finance.dispatch.worker.dto.response.MarketNews;
import com.finance.dispatch.worker.service.task.MarketNewsCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cache")
public class CacheController {

    private final MarketNewsCacheService marketNewsCacheService;

    @GetMapping("/fetch-market-event")
    public List<MarketNews> onCache_RetrievingMarketNews() {
        return marketNewsCacheService.onTask_RetrievingMarketNews();
    }

    @GetMapping("/reload-market-event")
    public void reload_MarketNews() {
        marketNewsCacheService.refresh();
    }

    @GetMapping("/clear-market-event")
    public void clear_MarketNews() {
        marketNewsCacheService.clear();
    }

}
