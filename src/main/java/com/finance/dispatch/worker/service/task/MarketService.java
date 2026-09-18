package com.finance.dispatch.worker.service.task;

import com.finance.dispatch.worker.config.properties.PublicUrlProperties;
import com.finance.dispatch.worker.constant.TypeConstant;
import com.finance.dispatch.worker.dto.request.GoldPriceRequest;
import com.finance.dispatch.worker.dto.response.ApiGoldPriceResponse;
import com.finance.dispatch.worker.entity.MarketHistory;
import com.finance.dispatch.worker.exception.LogicException;
import com.finance.dispatch.worker.repository.MarketHistoryRepository;
import com.finance.dispatch.worker.service.TelegramService;
import com.finance.dispatch.worker.util.DateTimeUtils;
import com.finance.dispatch.worker.util.RestClientHttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarketService {

    private final TelegramService telegramService;
    private final RestClientHttpUtils restClientHttpUtils;
    private final PublicUrlProperties publicUrlProperties;
    private final MarketHistoryRepository marketHistoryRepository;

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
                changeMsg+= "-" + change;
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

    public void onTask_TrackingGoldPriceApi(String type) {
        var date = DateTimeUtils.convertSimpleDate();
        log.info("[cron] onTask_TrackingGoldPriceApi {} executed for {}", type, date);

        this.onLogic_MarketHistoryData(
                type,
                date,
                this.onApi_RetrievePrice(),
                "USD"
        );
    }

    public void onTask_TrackingGoldPriceCallBack(GoldPriceRequest request) {

        var date = DateTimeUtils.convertSimpleDate();
        var type = request.getStatus();
        var goldPrice = request.getPrice();
        var symbol = request.getSymbol();

        log.info("[callback cron] onTask_TrackingGoldPrice {} executed for {}", type, date);

        this.onLogic_MarketHistoryData(
                date,
                type,
                goldPrice,
                symbol
        );
    }

    private void onLogic_MarketHistoryData(String type, String date, BigDecimal goldPrice, String symbol) {
        if(TypeConstant.OPENED.equals(type)){
            MarketHistory marketHistory = MarketHistory.builder()
                    .date(date)
                    .opened(goldPrice)
                    .closed(BigDecimal.ZERO)
                    .priceChange(BigDecimal.ZERO)
                    .createdAt(LocalDateTime.now())
                    .symbol(symbol)
                    .build();

            try {
                marketHistoryRepository.save(marketHistory);
                log.info("{} saved market history", type);
            } catch (Exception e) {
                log.error("Failed to save market history: {}", e.getMessage());
            }

        } else if(TypeConstant.CLOSED.equals(type)){
            MarketHistory marketHistory = marketHistoryRepository.findByDate(date);

            if(Objects.isNull(marketHistory)){
                log.error("Market History not found");
                return;
            }
            var openedPrice = marketHistory.getOpened();

            if(Objects.isNull(openedPrice)){
                marketHistoryRepository.delete(marketHistory);
                log.info("Deleted market history -> existing record will be deleted {}", date);
                return;
            }

            marketHistory.setClosed(goldPrice);
            marketHistory.setUpdatedAt(LocalDateTime.now());
            marketHistory.setPriceChange(marketHistory.getClosed().subtract(openedPrice));

            marketHistoryRepository.save(marketHistory);
            log.info("{} saved on existing market history {}", type, date);
        } else {
            throw new LogicException("Type " + type + " not found ");
        }
    }

}
