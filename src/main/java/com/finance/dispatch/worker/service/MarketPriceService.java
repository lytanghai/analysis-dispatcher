package com.finance.dispatch.worker.service;

import com.finance.dispatch.worker.constant.TypeConstant;
import com.finance.dispatch.worker.dto.request.MarketPriceFilter;
import com.finance.dispatch.worker.dto.request.MarketPriceRequest;
import com.finance.dispatch.worker.dto.response.MarketPriceResponse;
import com.finance.dispatch.worker.dto.response.PageResponse;
import com.finance.dispatch.worker.entity.MarketPrice;
import com.finance.dispatch.worker.repository.MarketPriceRepository;
import com.finance.dispatch.worker.repository.specification.MarketPriceSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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

}
