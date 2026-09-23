package com.finance.dispatch.worker.service;

import com.finance.dispatch.worker.constant.TypeConstant;
import com.finance.dispatch.worker.dto.request.MarketPriceFilter;
import com.finance.dispatch.worker.dto.request.MarketPriceRequest;
import com.finance.dispatch.worker.dto.response.MarketPriceResponse;
import com.finance.dispatch.worker.dto.response.PageResponse;
import com.finance.dispatch.worker.entity.MarketPrice;
import com.finance.dispatch.worker.records.Insight;
import com.finance.dispatch.worker.repository.MarketPriceRepository;
import com.finance.dispatch.worker.repository.specification.MarketPriceSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarketPriceService {

    private final MarketPriceRepository marketPriceRepository;

    public void batchInsert(List<MarketPriceRequest> request) {
        log.info("save batch request {}", request.size());

        List<MarketPrice> data = request.stream()
                .map(item -> {
                    MarketPrice marketPrice = new MarketPrice();

                    marketPrice.setDate(item.getDate());
                    marketPrice.setSymbol(TypeConstant.XAU);
                    marketPrice.setOpened(item.getOpened());
                    marketPrice.setClosed(item.getClosed());
                    marketPrice.setHighest(item.getHighest());
                    marketPrice.setLowest(item.getLowest());
                    marketPrice.setChanged(item.getHighest().subtract(item.getLowest()));

                    if (Objects.nonNull(item.getEvent())) {
                        marketPrice.setEvent(item.getEvent());
                    }

                    return marketPrice;
                })
                .toList();

        marketPriceRepository.saveAll(data);
    }

    public PageResponse<MarketPriceResponse> filterMarketPrice(MarketPriceFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<MarketPriceResponse> result = marketPriceRepository
                .findAll(MarketPriceSpecification.filter(filter), pageable
                ).map(f -> {
                    MarketPriceResponse response = new MarketPriceResponse();

                    response.setId(f.getId());
                    response.setDate(f.getDate());
                    response.setOpened(f.getOpened());
                    response.setClosed(f.getClosed());
                    response.setHighest(f.getHighest());
                    response.setLowest(f.getLowest());
                    response.setChanged(f.getHighest().subtract(f.getLowest()));
                    response.setEvent(f.getEvent());

                    return response;
                });

        return PageResponse.<MarketPriceResponse>builder()
                .content(result.getContent())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .size(result.getSize())
                .numberOfElements(result.getNumberOfElements())
                .build();
    }

    public void clear() {
        marketPriceRepository.deleteAll();
    }

    public List<Insight> insight(Integer days) {

        if (days == null || days <= 0) {
            days = 365;
        }

        List<MarketPrice> result = marketPriceRepository.findByDays(days);

        Insight highest = result.stream()
                .max(Comparator.comparing(MarketPrice::getHighest))
                .map(m -> new Insight(
                        m.getDate(),
                        m.getHighest(),
                        m.getLowest(),
                        m.getChanged(),
                        m.getEvent()
                ))
                .orElse(null);

        Insight lowest = result.stream()
                .min(Comparator.comparing(MarketPrice::getLowest))
                .map(m -> new Insight(
                        m.getDate(),
                        m.getHighest(),
                        m.getLowest(),
                        m.getChanged(),
                        m.getEvent()
                ))
                .orElse(null);

        Insight mostChanged = result.stream()
                .max(Comparator.comparing(MarketPrice::getChanged))
                .map(m -> new Insight(
                        m.getDate(),
                        m.getHighest(),
                        m.getLowest(),
                        m.getChanged(),
                        m.getEvent()
                ))
                .orElse(null);

        return Arrays.asList(highest, lowest, mostChanged);
    }

}
