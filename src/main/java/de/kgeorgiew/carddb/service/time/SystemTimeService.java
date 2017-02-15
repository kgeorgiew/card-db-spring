package de.kgeorgiew.carddb.service.time;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * @author kgeorgiew
 */
@Service
public class SystemTimeService implements TimeService {

    private final Clock clock = Clock.systemDefaultZone();

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
        return ZonedDateTime.now(clock);
    }

}
