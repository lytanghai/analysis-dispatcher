package com.finance.dispatch.worker.mapper;

import com.finance.dispatch.worker.dto.response.JobResponse;
import com.finance.dispatch.worker.entity.ScheduledJob;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
public interface JobMapper {

    JobResponse toResponse(ScheduledJob job);
}
