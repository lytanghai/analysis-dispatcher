package com.finance.dispatch.worker.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobFilterRequest {

    private Long id;

    private String jobName;

    private Boolean enabled;
}