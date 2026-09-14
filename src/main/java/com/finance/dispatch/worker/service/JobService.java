package com.finance.dispatch.worker.service;

import com.finance.dispatch.worker.constant.ResponseCode;
import com.finance.dispatch.worker.dto.request.JobFilterRequest;
import com.finance.dispatch.worker.dto.request.JobOperationRequest;
import com.finance.dispatch.worker.dto.request.JobRequest;
import com.finance.dispatch.worker.dto.response.JobResponse;
import com.finance.dispatch.worker.dto.response.PageResponse;
import com.finance.dispatch.worker.entity.ScheduledJob;
import com.finance.dispatch.worker.exception.DatabaseException;
import com.finance.dispatch.worker.mapper.JobMapper;
import com.finance.dispatch.worker.repository.ScheduledJobRepository;
import com.finance.dispatch.worker.repository.specification.ScheduledJobSpecification;
import com.finance.dispatch.worker.util.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobService {

    private final JobMapper jobMapper;
    private final ScheduledJobRepository scheduledJobRepository;

    public JobResponse createJob(JobRequest jobRequest) {

        var jobName = jobRequest.getJobName();

        ScheduledJob scheduledJob = scheduledJobRepository.findByJobName(jobName);
        if(Objects.nonNull(scheduledJob)){
            throw new DatabaseException(
                    ResponseCode.JOB_ALREADY_EXIST,
                    "Job already exists: " + jobName
            );
        }

        ScheduledJob newScheduledJob = ScheduledJob.builder()
                .jobName(jobName)
                .enabled(Boolean.TRUE)
                .createdAt(LocalDateTime.now())
                .cronExpression(jobRequest.getCronExpression())
                .build();

        try {
            newScheduledJob = scheduledJobRepository.save(newScheduledJob);
        }catch (Exception e){
            throw new DatabaseException(ResponseCode.FAILED_TO_CREATE, e.getMessage());
        }

        return jobMapper.toMapCreateNewJobResponse(
                newScheduledJob,
                DateTimeUtils.convert(newScheduledJob.getCreatedAt())
        );
    }

    @Transactional
    public void deleteJob(JobOperationRequest jobOperationRequest) {
        var jobId =  jobOperationRequest.getId();
        var jobName =  jobOperationRequest.getJobName();

        var msg = "Scheduled Job Deleted: %s";

        if(Objects.nonNull(jobId)){
            try {
                scheduledJobRepository.deleteById(jobId);
                log.info(String.format(msg, jobId));
            }catch (Exception e){
                throw new DatabaseException(ResponseCode.FAILED_TO_DELETE, e.getMessage());
            }

        }
        if(Objects.nonNull(jobName)){
            try {
                scheduledJobRepository.deleteByJobName(jobName);
                log.info(String.format(msg, jobId));
            }catch (Exception e){
                throw new DatabaseException(ResponseCode.FAILED_TO_DELETE, e.getMessage());
            }
        }
    }

    public void updateJob(JobOperationRequest jobOperationRequest) {
        var jobId = jobOperationRequest.getId();
        var jobName = jobOperationRequest.getJobName();
        var enabled = jobOperationRequest.isEnabled();

        ScheduledJob scheduledJob = scheduledJobRepository.findById(jobId).orElse(null);

        if(Objects.isNull(scheduledJob)){
            throw new DatabaseException(
                    ResponseCode.JOB_NOT_FOUND,
                    "Job not found: " + jobId
            );
        }

        if(!scheduledJob.getEnabled().equals(enabled)){
            scheduledJob.setEnabled(enabled);
        }

        if(Objects.nonNull(jobName)) {
            scheduledJob.setJobName(jobName);
        }

        scheduledJob.setUpdatedAt(LocalDateTime.now());

        scheduledJobRepository.save(scheduledJob);
        log.info("Updated Scheduled Job Id: " + jobId);

    }

    public PageResponse<JobResponse> getJobs(JobFilterRequest filter, int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<JobResponse> result = scheduledJobRepository
                .findAll(
                        ScheduledJobSpecification.filter(filter),
                        pageable
                )
                .map(jobMapper::toResponse);

        return PageResponse.<JobResponse>builder()
                .content(result.getContent())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .size(result.getSize())
                .numberOfElements(result.getNumberOfElements())
                .build();
    }

}
