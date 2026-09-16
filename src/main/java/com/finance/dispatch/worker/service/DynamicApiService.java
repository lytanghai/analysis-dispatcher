package com.finance.dispatch.worker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.dispatch.worker.dto.request.DynamicRequest;
import com.finance.dispatch.worker.dto.response.DynamicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class DynamicApiService {

    private final RestClient.Builder restClientBuilder;
    private final ObjectMapper objectMapper;

    public DynamicResponse execute(DynamicRequest request) {

        RestClient restClient = restClientBuilder.build();

        RestClient.RequestBodySpec requestSpec = restClient
                .method(HttpMethod.valueOf(request.getMethod().toUpperCase()))
                .uri(request.getUrl());

        // Headers
        if (request.getHeaders() != null) {
            request.getHeaders().forEach(requestSpec::header);
        }

        // Execute request
        ResponseEntity<String> response;

        if (request.getBody() != null) {
            response = requestSpec
                    .body(request.getBody())
                    .retrieve()
                    .toEntity(String.class);
        } else {
            response = requestSpec
                    .retrieve()
                    .toEntity(String.class);
        }

        // Convert response body
        Object body = parseResponseBody(response.getBody());

        return new DynamicResponse(
                response.getStatusCode().value(),
                response.getHeaders().toSingleValueMap(),
                body
        );
    }

    private Object parseResponseBody(String responseBody) {

        if (responseBody == null || responseBody.isBlank()) {
            return null;
        }

        try {
            return objectMapper.readValue(
                    responseBody,
                    Object.class
            );
        } catch (JsonProcessingException e) {
            // Response is not JSON, return it as plain text
            return responseBody;
        }
    }
}