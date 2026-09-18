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
import com.finance.dispatch.worker.util.Validator;
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

        Validator.validateCronExpress(jobRequest.getCronExpression());

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
            log.info("new job created: {}", newScheduledJob.getJobName());
        }catch (Exception e){
            throw new DatabaseException(ResponseCode.FAILED_TO_CREATE, e.getMessage());
        }

        JobResponse jobResponse = new JobResponse();
        jobResponse.setId(newScheduledJob.getId());
        jobResponse.setJobName(jobName);
        jobResponse.setCronExpression(newScheduledJob.getCronExpression());
        jobResponse.setEnabled(newScheduledJob.getEnabled());
        jobResponse.setCreatedAt(newScheduledJob.getCreatedAt());

        return jobResponse;
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
        var expression = jobOperationRequest.getCronExpression();

        Validator.validateCronExpress(expression);

        ScheduledJob scheduledJob = scheduledJobRepository.findById(jobId).orElse(null);

        if(Objects.isNull(scheduledJob)){
            throw new DatabaseException(
                    ResponseCode.JOB_NOT_FOUND,
                    "Job not found: " + jobId
            );
        }

        if(Objects.nonNull(expression)){
            scheduledJob.setCronExpression(expression);
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
                .map(f -> {
                    JobResponse response = new JobResponse();

                    response.setId(f.getId());
                    response.setCreatedAt(f.getCreatedAt());
                    response.setJobName(f.getJobName());
                    response.setCronExpression(f.getCronExpression());
                    response.setEnabled(f.getEnabled());
                    response.setUpdatedAt(f.getUpdatedAt());

                    return response;
                });

        return PageResponse.<JobResponse>builder()
                .content(result.getContent())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .size(result.getSize())
                .numberOfElements(result.getNumberOfElements())
                .build();
    }

}
