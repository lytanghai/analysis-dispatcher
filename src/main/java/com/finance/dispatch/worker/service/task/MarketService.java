package com.finance.dispatch.worker.service.task;

import com.finance.dispatch.worker.config.properties.PublicUrlProperties;
import com.finance.dispatch.worker.constant.TypeConstant;
import com.finance.dispatch.worker.dto.response.ApiGoldPriceResponse;
import com.finance.dispatch.worker.service.TelegramService;
import com.finance.dispatch.worker.util.RestClientHttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarketService {

    private final TelegramService telegramService;
    private final RestClientHttpUtils restClientHttpUtils;
    private final PublicUrlProperties publicUrlProperties;

    private BigDecimal PREVIOUS_PRICE = BigDecimal.ZERO;

    private BigDecimal onApi_RetrievePrice() {
        ApiGoldPriceResponse response = restClientHttpUtils.get(
                TypeConstant.DEFAULT_REQUESTER,
                publicUrlProperties.getXauPrice(),
                ApiGoldPriceResponse.class
        );

        if(Objects.isNull(response)) {
            log.error("[onApi_RetrievePrice] Failed to retrieve price");
            return BigDecimal.ZERO;
        }

        return response.getXau().getPrice();
    }

    public void onTask_RetrievingPriceUpdate(){
        BigDecimal currentPrice = this.onApi_RetrievePrice();

        if(currentPrice.equals(BigDecimal.ZERO)) {
            log.info("failed to fetch price");
            return;
        }
        log.info("price {}", currentPrice);

        var message = "";

        if(!PREVIOUS_PRICE.equals(BigDecimal.ZERO)) {
            var changeMsg = "";
            var change = currentPrice.subtract(PREVIOUS_PRICE);

            if (currentPrice.compareTo(PREVIOUS_PRICE) >= 0) {
                changeMsg+= "+" + change;
            } else {
                changeMsg+= change;
            }
            message = """
                    current: %.2f
                    previous: %.2f
                    change: %s
                    """.formatted(
                            currentPrice,
                            PREVIOUS_PRICE,
                            changeMsg
                     );
        } else {
            message = """
                    current: %.2f
                    """.formatted(
                            currentPrice
                    );
        }

        telegramService.sendMessage(message);
        PREVIOUS_PRICE = currentPrice;
    }

}
