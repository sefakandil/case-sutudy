package com.example.demo.config;


import java.time.Duration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "app.cache")
public class CacheProperties {

    private Duration ttl = Duration.ofMinutes(10);
    private boolean nullValues = false;

}
