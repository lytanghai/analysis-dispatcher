package com.finance.dispatch.worker.repository.specification;

import com.finance.dispatch.worker.dto.request.MarketPriceFilter;
import com.finance.dispatch.worker.entity.MarketPrice;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class MarketPriceSpecification {

    private MarketPriceSpecification() {
    }

    public static Specification<MarketPrice> filter(MarketPriceFilter request) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(
                        cb.equal(root.get("id"), request.getId())
                );
            }

            if (StringUtils.hasText(request.getEvent())) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("event")),
                                "%" + request.getEvent().toLowerCase() + "%"
                        )
                );
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}