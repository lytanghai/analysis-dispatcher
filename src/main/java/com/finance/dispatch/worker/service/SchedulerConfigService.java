package com.finance.dispatch.worker.service;

import com.finance.dispatch.worker.entity.ScheduledJob;
import com.finance.dispatch.worker.records.SchedulerConfigSnapshot;
import com.finance.dispatch.worker.repository.ScheduledJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerConfigService {
    private final ScheduledJobRepository scheduledJobRepository;

    private volatile String cachedHash;
    private static final ZoneId PHNOM_PENH = ZoneId.of("Asia/Phnom_Penh");

    private volatile List<ScheduledJob> cachedJobs;

    private volatile Instant cacheExpiresAt = Instant.MIN;

    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    public SchedulerConfigSnapshot getConfig() {

        if (!isCacheExpired()) {
            return new SchedulerConfigSnapshot(
                    cachedJobs,
                    cachedHash
            );
        }

        synchronized (this) {

            // Double-check after acquiring lock.
            if (!isCacheExpired()) {
                return new SchedulerConfigSnapshot(
                        cachedJobs,
                        cachedHash
                );
            }

            return refresh();
        }
    }

    private SchedulerConfigSnapshot refresh() {

        List<ScheduledJob> jobs = scheduledJobRepository.findAllByEnabledTrue();

        String hash = calculateHash(jobs);

        cachedJobs = List.copyOf(jobs);
        cachedHash = hash;
        cacheExpiresAt = Instant.now().plus(CACHE_TTL);

        log.info("Scheduler configuration cache refreshed. jobs={}, hash={}, expiresAt={}",
                jobs.size(),
                hash,
                cacheExpiresAt.atZone(PHNOM_PENH)
        );

        return new SchedulerConfigSnapshot(cachedJobs, cachedHash);
    }

    private boolean isCacheExpired() {
        return cachedJobs == null || Instant.now().isAfter(cacheExpiresAt);
    }

    private String calculateHash(List<ScheduledJob> jobs) {

        String content = jobs.stream()
                .sorted(Comparator.comparing(ScheduledJob::getId))
                .map(job -> String.join(
                        "|",
                        String.valueOf(job.getId()),
                        job.getJobName(),
                        job.getCronExpression(),
                        String.valueOf(job.getEnabled())
                ))
                .collect(Collectors.joining(";"));

        return DigestUtils.sha256Hex(content);
    }
}