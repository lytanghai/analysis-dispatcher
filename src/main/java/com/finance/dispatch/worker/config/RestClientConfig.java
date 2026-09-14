package com.finance.dispatch.worker.config;


import com.finance.dispatch.worker.config.properties.RestClientProperties;
import com.finance.dispatch.worker.interceptor.LoggingInterceptor;
import com.finance.dispatch.worker.util.RestClientHttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.TlsSocketStrategy;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.security.GeneralSecurityException;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Configuration
@ConditionalOnClass(RestClient.class)
@EnableConfigurationProperties(RestClientProperties.class)
public class RestClientConfig {

    @Bean
    @ConditionalOnMissingBean
    public RestClientHttpUtils restClientUtils(RestClientProperties properties) {
        return new RestClientHttpUtils(restClients(properties), properties.defaultClientName());
    }

    private Map<String, RestClient> restClients(RestClientProperties properties) {
        Map<String, RestClient> restClients = new LinkedHashMap<>();
        properties
                .clients()
                .forEach((clientName, clientProperties) ->
                        restClients.put(clientName, restClient(clientProperties, properties)));
        return restClients;
    }

    private RestClient restClient(RestClientProperties.Client clientProperties, RestClientProperties properties) {
        RestClient.Builder builder = RestClient.builder().requestFactory(requestFactory(clientProperties));

        if (StringUtils.hasText(clientProperties.getBaseUrl())) {
            builder.baseUrl(clientProperties.getBaseUrl());
        }

        if (loggingEnabled(properties)) {
            builder.requestInterceptor(new LoggingInterceptor());
        }

        return builder.build();
    }

    private HttpComponentsClientHttpRequestFactory requestFactory(RestClientProperties.Client clientProperties) {
        PoolingHttpClientConnectionManager connectionManager = connectionManager(clientProperties);
        connectionManager.setMaxTotal(
                Math.max(1, clientProperties.getConnectionPool().getMaxConnTotal()));
        connectionManager.setDefaultMaxPerRoute(
                Math.max(1, clientProperties.getConnectionPool().getMaxConnPerRoute()));

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectionRequestTimeout(timeout(clientProperties.getConnectionRequestTimeout()))
                        .setResponseTimeout(timeout(clientProperties.getReadTimeout()))
                        .build())
                .evictIdleConnections(
                        timeout(clientProperties.getConnectionPool().getEvictIdleConnections()))
                .evictExpiredConnections()
                .build();

        var factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);
        factory.setConnectionRequestTimeout(clientProperties.getConnectionRequestTimeout());
        factory.setReadTimeout(clientProperties.getReadTimeout());
        return factory;
    }

    private PoolingHttpClientConnectionManager connectionManager(RestClientProperties.Client clientProperties) {
        RestClientProperties.ConnectionPool pool = clientProperties.getConnectionPool();
        return PoolingHttpClientConnectionManagerBuilder.create()
                .setMaxConnTotal(pool.getMaxConnTotal())
                .setMaxConnPerRoute(pool.getMaxConnPerRoute())
                .setDefaultConnectionConfig(ConnectionConfig.custom()
                        .setValidateAfterInactivity(timeout(pool.getValidateAfterInactivity()))
                        .setTimeToLive(timeout(pool.getTimeToLive()))
                        .setConnectTimeout(timeout(clientProperties.getConnectTimeout()))
                        .setSocketTimeout(timeout(clientProperties.getReadTimeout()))
                        .build())
                .setDefaultSocketConfig(SocketConfig.custom()
                        .setSoTimeout(timeout(clientProperties.getReadTimeout()))
                        .build())
                .setConnPoolPolicy(pool.getConnPoolPolicy())
                .setPoolConcurrencyPolicy(pool.getPoolConcurrencyPolicy())
                .setTlsSocketStrategy(clientProperties.isSslEnabled() ? tlsSocketStrategy() : null)
                .build();
    }

    private TlsSocketStrategy tlsSocketStrategy() {
        try {
            return ClientTlsStrategyBuilder.create()
                    .setSslContext(SSLContextBuilder.create()
                            .loadTrustMaterial(TrustAllStrategy.INSTANCE)
                            .build())
                    .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .buildClassic();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to configure REST client SSL", e);
        }
    }

    private Timeout timeout(java.time.Duration duration) {
        if (duration == null || duration.isNegative() || duration.isZero()) {
            return Timeout.DISABLED;
        }
        return Timeout.ofMilliseconds(duration.toMillis());
    }

    private boolean loggingEnabled(RestClientProperties properties) {
        return properties.getLogging() != null && properties.getLogging().isEnabled();
    }
}
