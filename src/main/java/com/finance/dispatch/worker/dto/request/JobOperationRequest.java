package com.finance.dispatch.worker.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JobOperationRequest {

    // delete + update
    @JsonProperty("id")
    private Long id;

    @JsonProperty("job_name")
    private String jobName;

    // update

    @JsonProperty("cron_expression")
    private String cronExpression;

    @JsonProperty("enabled")
    private boolean enabled;


}
