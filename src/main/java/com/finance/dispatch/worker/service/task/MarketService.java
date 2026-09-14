package com.finance.dispatch.worker.service.task;

import com.finance.dispatch.worker.config.properties.PublicUrlProperties;
import com.finance.dispatch.worker.constant.TypeConstant;
import com.finance.dispatch.worker.dto.response.GoldPriceResponse;
import com.finance.dispatch.worker.entity.MarketHistory;
import com.finance.dispatch.worker.exception.DatabaseException;
import com.finance.dispatch.worker.exception.LogicException;
import com.finance.dispatch.worker.exception.ServerException;
import com.finance.dispatch.worker.repository.MarketHistoryRepository;
import com.finance.dispatch.worker.util.DateTimeUtils;
import com.finance.dispatch.worker.util.RestClientHttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarketService {

    private final PublicUrlProperties publicUrlProperties;
    private final RestClientHttpUtils restClientHttpUtils;
    private final MarketHistoryRepository marketHistoryRepository;

    private GoldPriceResponse retrieveGoldPrice() {

        GoldPriceResponse response = restClientHttpUtils.get(
                "default-connector",
                publicUrlProperties.getXauPrice(),
                GoldPriceResponse.class
        );

        if(Objects.isNull(response)){
            throw new ServerException("Failed to retrieveGoldPrice");
        }

        return response;
    }

    public void onTask_TrackingGoldPrice(String type) {
        String date = DateTimeUtils.convertSimpleDate();

        log.info("[cron] onTask_TrackingGoldPrice {} executed for {}", type, date);
        GoldPriceResponse goldPriceResponse = retrieveGoldPrice();

        if(TypeConstant.OPENED.equals(type)){

            MarketHistory marketHistory = MarketHistory.builder()
                    .date(date)
                    .createdAt(LocalDateTime.now())
                    .opened(goldPriceResponse.getPrice())
                    .symbol(goldPriceResponse.getSymbol())
                    .build();

            marketHistoryRepository.save(marketHistory);
            log.info("{} saved market history", type);

        } else if(TypeConstant.CLOSED.equals(type)){
            MarketHistory marketHistory = marketHistoryRepository.findByDate(date);

            if(Objects.isNull(marketHistory)){
                throw new DatabaseException("Market Record is not found " + date);
            }

            marketHistory.setClosed(goldPriceResponse.getPrice());
            marketHistory.setUpdatedAt(LocalDateTime.now());
            marketHistory.setPriceChange(marketHistory.getClosed().subtract(marketHistory.getOpened()));

            marketHistoryRepository.save(marketHistory);

        } else {
            throw new LogicException("Type " + type + " not found ");
        }
    }

}
