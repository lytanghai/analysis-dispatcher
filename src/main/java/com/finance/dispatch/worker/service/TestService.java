package com.finance.dispatch.worker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class TestService {

    @Autowired
    private RestClient restClient;

    public String testGold() {
        return restClient.get()
                .uri("https://api.gold-api.com/price/XAU")
                .retrieve()
                .body(String.class);

    }
}
