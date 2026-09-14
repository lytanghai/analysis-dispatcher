package com.finance.dispatch.worker.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JobRequest {

    @NotBlank
    @JsonProperty("job_name")
    private String jobName;

    @NotBlank
    @JsonProperty("cron_expression")
    private String cronExpression;

}
