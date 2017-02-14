package de.kgeorgiew.carddb.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.kgeorgiew.carddb.service.SystemTimeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.StringReader;
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
 * @author kgeorgiew
 */
@RunWith(SpringRunner.class)
@JsonTest
public class JacksonDateTest {

    @Autowired
    private JacksonTester<ZonedDateTime> zonedDateTimeConverter;

    @Autowired
    private JacksonTester<LocalDateTime> localDateTimeConverter;

    private LocalDateTime fixedLocalDateTime;
    private ZonedDateTime fixedZonedDateTime;

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
//        objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        Clock fixedClock = Clock.fixed(Instant.ofEpochMilli(1234567890), ZoneId.systemDefault());
        SystemTimeService fixedTimeService = new SystemTimeService(fixedClock);
        fixedLocalDateTime = fixedTimeService.asLocalDateTime();
        fixedZonedDateTime = fixedTimeService.asZonedDateTime();
    }

    @Test
    public void deserializeZonedDateTimeShouldBeInExpectedFormat() throws Exception {
        String zonedDateTime = fixedZonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        ZonedDateTime actual = zonedDateTimeConverter.parseObject("\"" + zonedDateTime + "\"");
        ZonedDateTime expected = fixedZonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));

        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void serializeZonedDateTimeShouldBeInExpectedFormat() throws Exception {
        String actual = zonedDateTimeConverter.write(fixedZonedDateTime).getJson();
        String expected = fixedZonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        assertThat(actual, is(equalTo("\"" + expected + "\"")));
    }

    // TODO Test fails in travis
    @Test
    public void deserializeLocalDateTimeShouldBeInExpectedFormat() throws Exception {
        String localDateTime = fixedLocalDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        LocalDateTime actual = localDateTimeConverter.parseObject("\"" + localDateTime + "\"");
        LocalDateTime expected = fixedLocalDateTime;

        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void serializeLocalDateTimeShouldBeInExpectedFormat() throws Exception {
        String actual = localDateTimeConverter.write(fixedLocalDateTime).getJson();
        String expected = fixedLocalDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        assertThat(actual, is(equalTo("\"" + expected + "\"")));
    }

    // TODO Test deserialization format with 'Z'
}
