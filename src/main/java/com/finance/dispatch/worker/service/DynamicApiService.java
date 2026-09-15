package com.finance.dispatch.worker.service;

import com.finance.dispatch.worker.dto.request.DynamicRequest;
import com.finance.dispatch.worker.dto.response.DynamicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class DynamicApiService {
    private final RestClient.Builder restClientBuilder;
    public DynamicResponse execute(DynamicRequest request) {
        RestClient restClient = restClientBuilder.build(); RestClient.RequestBodySpec requestSpec = restClient
                .method(HttpMethod.valueOf(request.getMethod().toUpperCase()))
                .uri(request.getUrl());
        if (request.getHeaders() != null) {
            request.getHeaders().forEach(requestSpec::header);
        } RestClient.ResponseSpec responseSpec;
        if (request.getBody() != null) {
            responseSpec = requestSpec .body(request.getBody()) .retrieve();
        } else {
            responseSpec = requestSpec.retrieve();
        }
        ResponseEntity<String> response = responseSpec.toEntity(String.class);
        return new DynamicResponse( response.getStatusCode().value(), response.getHeaders().toSingleValueMap(), response.getBody() );
    }
}