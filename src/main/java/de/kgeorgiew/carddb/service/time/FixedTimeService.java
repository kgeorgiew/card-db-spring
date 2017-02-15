package de.kgeorgiew.carddb.service.time;

import java.time.*;

/**
 * @author kgeorgiew
 */
public class FixedTimeService implements TimeService {

    private final Clock clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());

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
