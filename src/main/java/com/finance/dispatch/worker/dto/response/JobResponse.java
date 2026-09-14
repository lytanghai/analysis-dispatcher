package com.finance.dispatch.worker.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class JobResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("job_name")
    private String jobName;

    @JsonProperty("cron_expression")
    private String cronExpression;

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

}
