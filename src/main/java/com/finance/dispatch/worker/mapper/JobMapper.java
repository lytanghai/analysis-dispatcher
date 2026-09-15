package com.finance.dispatch.worker.mapper;

import com.finance.dispatch.worker.dto.response.JobResponse;
import com.finance.dispatch.worker.entity.ScheduledJob;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface JobMapper {

    JobResponse toMapCreateNewJobResponse(ScheduledJob scheduledJob, LocalDateTime createdAt);

    JobResponse toResponse(ScheduledJob scheduledJob);
}
