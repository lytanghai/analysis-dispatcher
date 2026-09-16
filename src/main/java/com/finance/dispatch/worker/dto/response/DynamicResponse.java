package com.finance.dispatch.worker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DynamicResponse {
    private int status;

    private Map<String, String> headers;

    private Object body;
}