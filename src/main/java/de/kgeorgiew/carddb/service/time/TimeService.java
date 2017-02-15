package de.kgeorgiew.carddb.service.time;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * @author kgeorgiew
 */
public interface TimeService {

    long asMillis();
    Instant asInstant();
    LocalDateTime asLocalDateTime();
    ZonedDateTime asZonedDateTime();
}
