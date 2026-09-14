package com.finance.dispatch.worker.config.properties;

import lombok.Data;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "custom.restclient")
public class RestClientProperties {

    public static final String DEFAULT_CLIENT_NAME = "default";

    private Logging logging = new Logging();
    private Map<String, Client> clients = new LinkedHashMap<>();

    public Map<String, Client> clients() {
        if (clients == null || clients.isEmpty()) {
            return Map.of(DEFAULT_CLIENT_NAME, new Client());
        }
        return clients;
    }

    public Client client(String clientName) {
        String resolvedClientName = resolveClientName(clientName);
        Client client = clients().get(resolvedClientName);
        if (client == null) {
            throw new IllegalArgumentException("REST client config not found: " + resolvedClientName);
        }
        return client;
    }

    public String defaultClientName() {
        Map<String, Client> configuredClients = clients();
        if (configuredClients.containsKey(DEFAULT_CLIENT_NAME)) {
            return DEFAULT_CLIENT_NAME;
        }
        return configuredClients.keySet().iterator().next();
    }

    private String resolveClientName(String clientName) {
        if (clientName == null || clientName.isBlank()) {
            return defaultClientName();
        }
        return clientName;
    }

    @Data
    public static class Client {
        private String baseUrl;
        private Duration connectTimeout = Duration.ofMillis(10000);
        private Duration readTimeout = Duration.ofMillis(15000);
        private Duration connectionRequestTimeout = Duration.ofMillis(10000);
        private ConnectionPool connectionPool = new ConnectionPool();
        private boolean sslEnabled = false;
    }

    @Data
    public static class ConnectionPool {
        private Integer maxConnTotal = 500;
        private Integer maxConnPerRoute = 100;
        private Duration validateAfterInactivity = Duration.ofMillis(2000);
        private Duration timeToLive = Duration.ofMillis(300000);
        private Duration evictIdleConnections = Duration.ofMillis(30_000);
        private PoolReusePolicy connPoolPolicy = PoolReusePolicy.LIFO;
        private PoolConcurrencyPolicy poolConcurrencyPolicy = PoolConcurrencyPolicy.STRICT;
    }

    @Data
    public static class Logging {
        private boolean enabled = false;
    }
}
