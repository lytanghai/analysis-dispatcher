package com.finance.dispatch.worker.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RestClientHttpUtils {

    private static final Logger log = LoggerFactory.getLogger(RestClientHttpUtils.class);
    private final Map<String, RestClient> restClients;
    private final String defaultClientName;

    private static String GET = "GET";
    private static String POST = "POST";

    public RestClientHttpUtils(Map<String, RestClient> restClients, String defaultClientName) {
        this.restClients = Map.copyOf(restClients);
        this.defaultClientName = defaultClientName;
    }

    /**
     * Executes a RestClient call wrapped in a standardized try-catch block.
     *
     * @param restClientName The configured rest client name
     * @param requestAction A lambda executing the specific HTTP method and path
     * @return Optional containing the response data, or Optional.empty() if failed
     */
    public <T> Optional<T> execute(String restClientName, Supplier<T> requestAction) {
        String resolvedClientName = resolveClientName(restClientName);
        try {
            return Optional.ofNullable(requestAction.get());
        } catch (ResourceAccessException e) {
            log.error("Network timeout or I/O error from REST client: {}", resolvedClientName, e);
        } catch (HttpClientErrorException e) {
            log.error(
                    "Client error (4xx) from REST client {}. Status: {}, Response: {}",
                    resolvedClientName,
                    e.getStatusCode(),
                    e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            log.error(
                    "Server error (5xx) from REST client {}. Status: {}, Response: {}",
                    resolvedClientName,
                    e.getStatusCode(),
                    e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Unexpected error during REST call from client: {}", resolvedClientName, e);
        }

        return Optional.empty();
    }

    private <T> T executeAndLog(
            String clientName, String method, String uri, Supplier<ResponseEntity<T>> requestAction) {

        long startTime = System.currentTimeMillis();
        try {
            log.info("[{}] Request started. Client: {}, URL: {}", method, resolveClientName(clientName), uri);

            ResponseEntity<T> response = requestAction.get();
            long duration = System.currentTimeMillis() - startTime;
            log.info(
                    "[{}] Request completed. URL: {}, Status: {}, Duration: {} ms ({} sec)",
                    method,
                    uri,
                    response.getStatusCode().value(),
                    duration,
                    String.format("%.3f", duration / 1000.0));

            return response.getBody();
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("[{}] Request failed. URL: {}, Duration: {} ms", method, uri, duration, e);
            throw e;
        }
    }

    /**
     * Executes a call using the default rest client.
     */
    public <T> Optional<T> execute(Supplier<T> requestAction) {
        return execute(defaultClientName, requestAction);
    }

    /**
     * Returns a pre-configured RestClient by restclientName.
     */
    public RestClient getClient(String restClientName) {
        String resolvedClientName = resolveClientName(restClientName);
        RestClient restClient = restClients.get(resolvedClientName);
        if (restClient == null) {
            throw new IllegalArgumentException("REST client config not found: " + resolvedClientName);
        }
        return restClient;
    }

    private <T> T executePost(String clientName, String uri, Object body, HttpHeaders headers, Class<T> responseType) {

        return executeAndLog(clientName, POST, uri, () -> {
            RestClient.RequestBodySpec request = getClient(clientName).post().uri(uri);

            if (body != null) request.body(body);

            HttpHeaders finalHeaders = buildHeader(headers);
            request.headers(h -> h.addAll(finalHeaders));

            return request.retrieve().toEntity(responseType);
        });
    }

    public <T> T post(String clientName, String uri, Object payload, HttpHeaders headers, Class<T> responseType) {
        return executePost(clientName, uri, payload, headers, responseType);
    }

    public <T> T post(String clientName, String uri, Object payload, Class<T> responseType) {
        return executePost(clientName, uri, payload, null, responseType);
    }

    /***
     * GET method
     */
    public <T> T get(String clientName, String uri, Class<T> responseType) {
        return executeAndLog(clientName, GET, uri, () -> {
            var request = getClient(clientName)
                    .get()
                    .uri(uri);

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            request.headers(h -> h.addAll(headers));

            return request.retrieve().toEntity(responseType);
        });
    }

    public <T> T get(
            String clientName,
            String uri,
            ParameterizedTypeReference<T> responseType
    ) {
        return executeAndLog(clientName, GET, uri, () -> {
            var request = getClient(clientName)
                    .get()
                    .uri(uri);

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            request.headers(h -> h.addAll(headers));

            return request.retrieve().toEntity(responseType);
        });
    }

    private HttpHeaders buildHeader(HttpHeaders requestHeaders) {
        HttpHeaders headers = requestHeaders == null ? new HttpHeaders() : requestHeaders;
        if (requestHeaders == null) {
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        }
        return headers;
    }

    /**
     * Returns the default pre-configured RestClient.
     */
    public RestClient getClient() {
        return getClient(defaultClientName);
    }

    private String resolveClientName(String restClientName) {
        if (restClientName == null || restClientName.isBlank()) {
            return defaultClientName;
        }
        return restClientName;
    }
}
