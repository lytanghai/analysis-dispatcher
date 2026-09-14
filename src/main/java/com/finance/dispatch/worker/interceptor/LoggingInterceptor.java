package com.finance.dispatch.worker.interceptor;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    @NonNull
    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, byte @NonNull [] body, ClientHttpRequestExecution execution) throws IOException {
        Instant startTime = Instant.now();
        log.debug(
                "URI: {}, Method: {}, Body: {}, Start Time: {}",
                request.getURI(),
                request.getMethod(),
                new String(body, StandardCharsets.UTF_8),
                startTime.toEpochMilli());

        ClientHttpResponse response = execution.execute(request, body);

        log.debug("Response Status: {}", response.getStatusCode());
        return response;
    }
}
