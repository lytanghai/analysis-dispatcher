package com.finance.dispatch.worker.service;

import com.finance.dispatch.worker.entity.ScheduledJob;
import com.finance.dispatch.worker.records.SchedulerConfigSnapshot;
import com.finance.dispatch.worker.service.task.MarketNewsCacheService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private static final String TIME_ZONE = "Asia/Phnom_Penh";

    private final TaskScheduler taskScheduler;
    private final SchedulerConfigService schedulerConfigService;
    private final MarketNewsCacheService marketNewsCacheService;

    private final Map<Long, ScheduledFuture<?>> scheduledTasks =
            new ConcurrentHashMap<>();

    private volatile String currentConfigHash;

    @PostConstruct
    public void initialize() {
        log.info("Initializing dynamic scheduler");
        refreshSchedules();
    }

    @Scheduled(fixedDelay = 60, timeUnit = TimeUnit.MINUTES)
    public void checkForConfigurationChanges() {
        log.info("Checking for scheduler configuration changes");
        refreshSchedules();
    }

    private void refreshSchedules() {

        SchedulerConfigSnapshot snapshot = schedulerConfigService.getConfig();

        if (Objects.equals(currentConfigHash, snapshot.hash())) {
            return;
        }

        log.info("Scheduler configuration changed. oldHash={}, newHash={}",
                currentConfigHash,
                snapshot.hash()
        );

        reconcileSchedules(snapshot.jobs());

        currentConfigHash = snapshot.hash();
    }

    private void reconcileSchedules(List<ScheduledJob> jobs) {

        Map<Long, ScheduledJob> newJobs = jobs.stream()
                .collect(Collectors.toMap(
                        ScheduledJob::getId,
                        Function.identity()
                ));

        // Cancel jobs that no longer exist.
        for (Long jobId : new ArrayList<>(scheduledTasks.keySet())) {

            if (!newJobs.containsKey(jobId)) {
                cancelJob(jobId);
            }
        }

        // Schedule/update jobs.
        for (ScheduledJob job : jobs) {
            scheduleJob(job);
        }
    }

    private void scheduleJob(ScheduledJob job) {
        cancelJob(job.getId());

        try {
            ZoneId zoneId = ZoneId.of("Asia/Phnom_Penh");

            CronTrigger trigger = new CronTrigger(
                    job.getCronExpression(),
                    TimeZone.getTimeZone(zoneId)
            );

            ScheduledFuture<?> future =
                    taskScheduler.schedule(() -> executeJob(job), trigger);

            scheduledTasks.put(job.getId(), future);

            log.info(
                    "Scheduled job. id={}, name={}, cron={}, timezone={}, serverNow={}, zoneIdNow={}",
                    job.getId(),
                    job.getJobName(),
                    job.getCronExpression(),
                    zoneId,
                    LocalDateTime.now(),
                    LocalDateTime.now(zoneId)
            );

        } catch (Exception e) {
            log.error(
                    "Failed to schedule job. id={}, name={}, cron={}",
                    job.getId(),
                    job.getJobName(),
                    job.getCronExpression(),
                    e
            );
        }
    }

    private void cancelJob(Long jobId) {

        ScheduledFuture<?> future = scheduledTasks.remove(jobId);

        if (future != null) {
            future.cancel(false);
            log.info("Cancelled scheduled job. id={}", jobId);
        }
    }

    private void executeJob(ScheduledJob job) {
        log.info("Executing scheduled job. id={}, name={}", job.getId(), job.getJobName());
        try {
            switch (job.getJobName()) {
//                case "XAU_OPEN_MARKET" ->
//                        marketService.onTask_TrackingGoldPrice(
//                                TypeConstant.OPENED
//                        );
//                case "XAU_CLOSE_MARKET" ->
//                        marketService.onTask_TrackingGoldPrice(
//                                TypeConstant.CLOSED
//                        );
                case "MARKET_NEWS" ->
                        marketNewsCacheService
                                .onTask_RetrievingMarketNews();
                default ->
                        log.warn("Unknown scheduled job. id={}, name={}",
                                job.getId(),
                                job.getJobName()
                        );
            }

        } catch (Exception e) {
            log.error("Scheduled job failed. id={}, name={}",
                    job.getId(),
                    job.getJobName(),
                    e
            );
        }
    }
}