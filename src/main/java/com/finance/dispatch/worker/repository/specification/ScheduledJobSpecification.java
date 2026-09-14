package com.finance.dispatch.worker.repository.specification;

import com.finance.dispatch.worker.dto.request.JobFilterRequest;
import com.finance.dispatch.worker.entity.ScheduledJob;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class ScheduledJobSpecification {

    private ScheduledJobSpecification() {
    }

    public static Specification<ScheduledJob> filter(JobFilterRequest request) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(
                        cb.equal(root.get("id"), request.getId())
                );
            }

            if (StringUtils.hasText(request.getJobName())) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("jobName")),
                                "%" + request.getJobName().toLowerCase() + "%"
                        )
                );
            }

            if (request.getEnabled() != null) {
                predicates.add(
                        cb.equal(
                                root.get("enabled"),
                                request.getEnabled()
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}