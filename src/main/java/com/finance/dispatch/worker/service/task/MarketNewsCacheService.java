package com.finance.dispatch.worker.service.task;

import com.finance.dispatch.worker.config.properties.PublicUrlProperties;
import com.finance.dispatch.worker.constant.CacheConstant;
import com.finance.dispatch.worker.dto.response.MarketNews;
import com.finance.dispatch.worker.exception.ServerException;
import com.finance.dispatch.worker.util.RestClientHttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarketNewsCacheService {

    private final CacheManager cacheManager;
    private final PublicUrlProperties publicUrlProperties;
    private final RestClientHttpUtils restClientHttpUtils;

    public List<MarketNews> onTask_RetrievingMarketNews() {
        List<MarketNews> marketNews = this.marketNewsCache().get(CacheConstant.CACHE_KEY, List.class);

        if(Objects.isNull(marketNews)){
            log.info("MarketNews is null");
            return List.of();
        }

        return marketNews
                .stream()
                .filter(news -> "USD".equalsIgnoreCase(news.getCountry()))
                .toList();
    }

    public List<MarketNews> retrieveForexFactory() {
        log.info("Requesting to Forex Factory API...");

        List<MarketNews> marketNews = restClientHttpUtils.get(
                "default-connector",
                publicUrlProperties.getForexFactory(),
                new ParameterizedTypeReference<List<MarketNews>>() {}
        );

        if (marketNews == null) {
            throw new ServerException("Failed to retrieve Forex Factory market news");
        }

        this.put(marketNews);

        return marketNews;
    }

    public void clear() {
        this.marketNewsCache().evict(CacheConstant.CACHE_KEY);
    }

    public void refresh() {
        this.retrieveForexFactory();
    }

    private void put(List<MarketNews> marketNews) {
        this.marketNewsCache().put(CacheConstant.CACHE_KEY, marketNews);
    }

    private Cache marketNewsCache() {
        return cacheManager.getCache(CacheConstant.CACHE_NAME);
    }
}