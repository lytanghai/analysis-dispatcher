package com.finance.dispatch.worker.config.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@ToString
@Validated
@Configuration
@ConfigurationProperties(prefix = "public-url")
public class PublicUrlProperties {

    private String xauPrice;

    private String forexFactory;

}
