package com.finance.dispatch.worker.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class DynamicRequest {

    @NotBlank
    private String url;

    @NotBlank
    private String method;

    private Map<String, String> headers;

    private Object body;
}

