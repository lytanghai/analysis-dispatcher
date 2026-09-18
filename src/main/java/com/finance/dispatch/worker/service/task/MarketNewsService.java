package com.finance.dispatch.worker.service.task;

import com.finance.dispatch.worker.config.properties.PublicUrlProperties;
import com.finance.dispatch.worker.constant.CacheConstant;
import com.finance.dispatch.worker.constant.TypeConstant;
import com.finance.dispatch.worker.dto.request.BotMessageRequest;
import com.finance.dispatch.worker.dto.response.MarketNews;
import com.finance.dispatch.worker.exception.ServerException;
import com.finance.dispatch.worker.service.TelegramService;
import com.finance.dispatch.worker.util.RestClientHttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarketNewsService {

    private final CacheManager cacheManager;
    private final TelegramService telegramService;
    private final PublicUrlProperties publicUrlProperties;
    private final RestClientHttpUtils restClientHttpUtils;

    public List<MarketNews> onTask_RetrievingMarketNews() {
        log.info("[cache] onTask_RetrievingMarketNews");

        return this.fetch()
                .stream()
                .filter(news -> "USD".equalsIgnoreCase(news.getCountry()))
                .toList();
    }

    public void onTask_RetrievingDailyMarketEvent() {
        List<MarketNews> marketNews = this.fetch();

        LocalDate today = LocalDate.now();

        List<MarketNews> todayUsdNews = marketNews.stream()
                .filter(news -> "USD".equalsIgnoreCase(news.getCountry()))
                .filter(news -> news.getDate() != null)
                .filter(news -> news.getDate().toLocalDate().equals(today))
                .toList();

        BotMessageRequest botMessageRequest = new BotMessageRequest();

        var tgMessage = todayUsdNews.isEmpty()
                ? "💵 <b>Today event</b>\n\nNo event today."
                : """
          💵 <b>Today event: %s</b>

          %s
          """.formatted(
                todayUsdNews.getFirst().getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                todayUsdNews.stream()
                        .map(news -> """
                                🕐 %s — %s | %s %s
                                """
                                .formatted(
                                news.getDate().format(DateTimeFormatter.ofPattern("hh:mm a")),
                                news.getTitle(),
                                getImpactEmoji(news.getImpact()),
                                news.getImpact()
                        ))
                        .collect(Collectors.joining("\n"))
        );
        telegramService.sendMessage(tgMessage);
        log.info("message sent!");
    }

    public String getImpactEmoji(String impact) {
        return switch (impact.toUpperCase()) {
            case "HIGH" -> "🔴";
            case "MEDIUM" -> "🟡";
            case "LOW" -> "🟢";
            default -> "⚪";
        };
    }

    public List<MarketNews> fetch() {
        List<MarketNews> marketNews = this.marketNewsCache().get(CacheConstant.THIS_WEEK, List.class);

        if(Objects.isNull(marketNews)){
            log.info("MarketNews is null");
            return List.of();
        }
        return marketNews;
    }

    public List<MarketNews> retrieveForexFactory() {
        log.info("Requesting to Forex Factory API...");

        List<MarketNews> marketNews = restClientHttpUtils.get(
                TypeConstant.DEFAULT_REQUESTER,
                publicUrlProperties.getForexFactory(),
                new ParameterizedTypeReference<>() {
                }
        );

        if (marketNews == null) {
            throw new ServerException("Failed to retrieve Forex Factory market news");
        }

        this.put(marketNews);

        return marketNews;
    }

    public void clear() {
        this.marketNewsCache().evict(CacheConstant.THIS_WEEK);
    }

    public void refresh() {
        this.retrieveForexFactory();
    }

    private void put(List<MarketNews> marketNews) {
        this.marketNewsCache().put(CacheConstant.THIS_WEEK, marketNews);
    }

    private Cache marketNewsCache() {
        return cacheManager.getCache(CacheConstant.MARKET_NEWS);
    }
}