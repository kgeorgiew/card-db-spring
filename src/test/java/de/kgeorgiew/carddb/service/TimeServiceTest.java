package de.kgeorgiew.carddb.service;

import de.kgeorgiew.carddb.service.time.FixedTimeService;
import de.kgeorgiew.carddb.service.time.SystemTimeService;
import de.kgeorgiew.carddb.service.time.TimeService;
import org.junit.Test;

import java.time.Clock;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author kgeorgiew
 */
public class TimeServiceTest {


    @Test
    public void shouldReturnSystemTime() throws Exception {
        Clock systemClock = Clock.systemDefaultZone();

        long timeBefore = systemClock.millis();
        Thread.sleep(1L);

        TimeService timeService = new SystemTimeService();
        long time = timeService.asMillis();

        Thread.sleep(1L);
        long timeAfter = System.currentTimeMillis();

        assertTrue("Time " + time + " should be between " + timeBefore + " and " + timeAfter,
                timeBefore < time && timeAfter > time);
    }

    @Test
    public void shouldReturnFakedTimeInMilliseconds() throws Exception {
        TimeService timeService = new FixedTimeService();

        long timeBefore = timeService.asMillis();

        Thread.sleep(1L);

        long timeAfter = timeService.asMillis();

        assertThat(timeBefore, equalTo(timeAfter));
    }
}
