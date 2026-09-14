package com.finance.dispatch.worker.records;

import com.finance.dispatch.worker.entity.ScheduledJob;

import java.util.List;

public record SchedulerConfigSnapshot(
        List<ScheduledJob> jobs,
        String hash
) {
}