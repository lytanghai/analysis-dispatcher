package com.finance.dispatch.worker.controller;

import com.finance.dispatch.worker.dto.response.GoldPriceResponse;
import com.finance.dispatch.worker.service.task.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/callback")
public class CallbackController {

    private final MarketService marketService;

    @PostMapping("/gold-price")
    public void onCallBack_retrievePrice(@RequestBody GoldPriceResponse request) {
        marketService.onTask_RetrievingPriceUpdate(request);
    }

}
