package com.finance.dispatch.worker.controller;

import com.finance.dispatch.worker.dto.request.GoldPriceRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/callback")
public class CallbackController {

    @PostMapping("/receive_price")
    public void onCallback_ReceivePrice(@RequestBody GoldPriceRequest request) {
      log.info("Receive price request : {}", request.getPrice());
    }

}
