package com.finance.dispatch.worker.service.task;

import com.finance.dispatch.worker.constant.TypeConstant;
import com.finance.dispatch.worker.dto.request.GoldPriceRequest;
import com.finance.dispatch.worker.entity.MarketHistory;
import com.finance.dispatch.worker.exception.LogicException;
import com.finance.dispatch.worker.repository.MarketHistoryRepository;
import com.finance.dispatch.worker.util.DateTimeUtils;
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

    private final MarketHistoryRepository marketHistoryRepository;

    public void onTask_TrackingGoldPrice(GoldPriceRequest request) {

        var date = DateTimeUtils.convertSimpleDate();
        var type = request.getStatus();
        var goldPrice = request.getPrice();
        var symbol = request.getSymbol();

        log.info("[cron] onTask_TrackingGoldPrice {} executed for {}", type, date);

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
