package de.kgeorgiew.carddb.json;

import de.kgeorgiew.carddb.service.SystemTimeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.*;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 *
 * Serialize LocalDateTime should result in format yyyy-MM-dd'T'H:mm:ss.SSS
 * Deserialize '2017-01-10T15:08:46.948Z' should result in LocalDateTime 2017-01-10T16:08:46.948
 *
 *
 * @author kgeorgiew
 */
@RunWith(SpringRunner.class)
@JsonTest
@ActiveProfiles({"test"})
public class JsonConvertTest {

    @Autowired
    private JacksonTester<ZonedDateTime> zonedDateTimeConverter;

    @Autowired
    private JacksonTester<LocalDateTime> localDateTimeConverter;


    private LocalDateTime fixedLocalDateTime;
    private ZonedDateTime fixedZonedDateTime;

    @Before
    public void setUp() {
        Clock fixedClock = Clock.fixed(Instant.ofEpochMilli(1234567890), ZoneId.systemDefault());
        SystemTimeService fixedTimeService = new SystemTimeService(fixedClock);
        fixedLocalDateTime = fixedTimeService.asLocalDateTime();
        fixedZonedDateTime = fixedTimeService.asZonedDateTime();
    }

    @Test
    public void deserializeZonedDateTimeShouldBeInExpectedFormat() throws Exception {
        String fixedDateTime = "1970-01-15T06:56:07.890Z";
        ZonedDateTime actual = zonedDateTimeConverter.parseObject("\"" + fixedDateTime + "\"");
        ZonedDateTime expected = fixedZonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));

        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void serializeZonedDateTimeShouldBeInExpectedFormat() throws Exception {
        String expected = fixedZonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
//        String expected = "1970-01-15T06:56:07.890Z";
        String actual = zonedDateTimeConverter.write(fixedZonedDateTime).getJson();

        assertThat(actual, is(equalTo("\"" + expected + "\"")));
    }

    @Test
    public void deserializeLocalDateTimeShouldBeInExpectedFormat() throws Exception {
        String fixedDateTime = "1970-01-15T07:56:07.890"; // Offset included
        LocalDateTime actual = localDateTimeConverter.parseObject("\"" + fixedDateTime + "\"");
        LocalDateTime expected = fixedLocalDateTime;

        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void serializeLocalDateTimeShouldBeInExpectedFormat() throws Exception {
        String expected = fixedLocalDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String actual = localDateTimeConverter.write(fixedLocalDateTime).getJson();

        assertThat(actual, is(equalTo("\"" + expected + "\"")));
    }

}