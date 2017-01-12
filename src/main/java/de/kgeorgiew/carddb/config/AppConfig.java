package de.kgeorgiew.carddb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * @author kgeorgiew
 */
@Configuration
public class AppConfig {


    @Bean("clock")
    public Clock getClock() {
        return Clock.systemDefaultZone();
    }
}
