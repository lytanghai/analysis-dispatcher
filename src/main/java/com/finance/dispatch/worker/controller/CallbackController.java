package com.finance.dispatch.worker.controller;

import com.finance.dispatch.worker.dto.request.GoldPriceRequest;
import com.finance.dispatch.worker.service.task.MarketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/callback")
public class CallbackController {

    private final MarketService marketService;

    @PostMapping("/receive_price")
    public void onCallback_ReceivePrice(@RequestBody GoldPriceRequest request) {
      marketService.onTask_TrackingGoldPrice(request);
    }

}
