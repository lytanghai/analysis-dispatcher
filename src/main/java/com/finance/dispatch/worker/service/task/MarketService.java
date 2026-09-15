package com.finance.dispatch.worker.service.task;

import com.finance.dispatch.worker.config.properties.PublicUrlProperties;
import com.finance.dispatch.worker.constant.TypeConstant;
import com.finance.dispatch.worker.dto.response.GoldPriceResponse;
import com.finance.dispatch.worker.entity.MarketHistory;
import com.finance.dispatch.worker.exception.LogicException;
import com.finance.dispatch.worker.exception.ServerException;
import com.finance.dispatch.worker.repository.MarketHistoryRepository;
import com.finance.dispatch.worker.util.DateTimeUtils;
import com.finance.dispatch.worker.util.RestClientHttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarketService {

    private final PublicUrlProperties publicUrlProperties;
    private final RestClientHttpUtils restClientHttpUtils;
    private final MarketHistoryRepository marketHistoryRepository;

    public GoldPriceResponse retrieveGoldPrice() {

        RestTemplate restTemplate = new RestTemplate();
        try {
            GoldPriceResponse response = restTemplate.getForObject(publicUrlProperties.getXauPrice(), GoldPriceResponse.class);
            if(Objects.isNull(response)){
                throw new ServerException("Failed to retrieveGoldPrice");
            }
            return response;
        }catch (Exception e){
            log.error("Failed to retrieveGoldPrice {}",e.getMessage());
        }
        log.error("Response Object Not Valid");
        return null;
    }

    public void onTask_TrackingGoldPrice(String type) {
        String date = DateTimeUtils.convertSimpleDate();

        log.info("[cron] onTask_TrackingGoldPrice {} executed for {}", type, date);
        GoldPriceResponse goldPriceResponse = retrieveGoldPrice();
        var goldPrice = goldPriceResponse.getPrice();

        if(TypeConstant.OPENED.equals(type)){
            MarketHistory marketHistory = MarketHistory.builder()
                    .date(date)
                    .opened(goldPrice)
                    .closed(BigDecimal.ZERO)
                    .priceChange(BigDecimal.ZERO)
                    .createdAt(LocalDateTime.now())
                    .symbol(goldPriceResponse.getSymbol())
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
