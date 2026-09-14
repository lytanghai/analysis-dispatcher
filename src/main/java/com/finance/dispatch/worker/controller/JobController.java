package com.finance.dispatch.worker.controller;

import com.finance.dispatch.worker.dto.request.JobFilterRequest;
import com.finance.dispatch.worker.dto.request.JobOperationRequest;
import com.finance.dispatch.worker.dto.request.JobRequest;
import com.finance.dispatch.worker.dto.response.JobResponse;
import com.finance.dispatch.worker.dto.response.PageResponse;
import com.finance.dispatch.worker.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/job")
public class JobController {

    private final JobService jobService;

    @PostMapping("/create")
    public JobResponse createJob(@RequestBody @Valid JobRequest jobRequest) {
        return jobService.createJob(jobRequest);
    }

    //update
    @PostMapping("/update")
    public void updateJob(@RequestBody @Valid JobOperationRequest jobOperationRequest) {
        jobService.updateJob(jobOperationRequest);
    }

    //delete
    @PostMapping("/delete")
    public void deleteJob(@RequestBody JobOperationRequest jobOperationRequest) {
        jobService.deleteJob(jobOperationRequest);
    }

    //list
    @GetMapping("/list")
    public ResponseEntity<PageResponse<JobResponse>> getJobs(
            @RequestBody JobFilterRequest jobFilterRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(jobService.getJobs(jobFilterRequest, page, size));
    }

}
