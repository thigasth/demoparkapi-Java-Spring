package com.thiago.demo_park_api.config;

import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;


@Configuration
public class SpringTimezoneConfig {

    @PostConstruct
    public void timezoneConfig() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
    }
}
