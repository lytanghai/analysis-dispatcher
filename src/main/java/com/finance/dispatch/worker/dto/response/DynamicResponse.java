package com.finance.dispatch.worker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class DynamicResponse {
    private int status;
    private Map<String, String> headers;
    private String body;
}