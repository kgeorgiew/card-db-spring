package de.kgeorgiew.carddb.service;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * @author kgeorgiew
 */

@Service
public class SystemTimeService {

    private Clock clock;

    public SystemTimeService(Clock clock) {
        this.clock = clock;
    }

    public long asMillis() {
        return clock.millis();
    }


    public Instant asInstant() {
        return clock.instant();
    }

    public LocalDateTime asLocalDateTime() {
        return LocalDateTime.ofInstant(asInstant(), clock.getZone());
    }

    public ZonedDateTime asZonedDateTime() {
        return ZonedDateTime.now(clock); //asLocalDateTime(), clock.getZone());
    }

}
