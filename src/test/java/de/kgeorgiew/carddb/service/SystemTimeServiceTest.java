package de.kgeorgiew.carddb.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author kgeorgiew
 */
public class SystemTimeServiceTest {

    @Test
    public void shouldReturnSystemTime() throws Exception {
        Clock systemClock = Clock.systemDefaultZone();

        long timeBefore = systemClock.millis();
        Thread.sleep(1L);

        SystemTimeService timeService = new SystemTimeService(systemClock);
        long time = timeService.asMillis();

        Thread.sleep(1L);
        long timeAfter = System.currentTimeMillis();

        assertTrue(time < timeAfter && time > timeBefore);
    }

    @Test
    public void shouldReturnFakedTimeInMilliseconds() throws Exception {
        Clock fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        long fakeTime = fixedClock.millis();

        SystemTimeService timeService = new SystemTimeService(fixedClock);

        assertEquals(timeService.asMillis(), fakeTime);

    }
}
