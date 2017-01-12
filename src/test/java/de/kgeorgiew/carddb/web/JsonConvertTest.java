package de.kgeorgiew.carddb.web;

import de.kgeorgiew.carddb.service.SystemTimeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
public class JsonConvertTest {

    @Autowired
    private JacksonTester<LocalDateTime> dateTimeParser;

    private LocalDateTime fixedDateTime;

    @Before
    public void setUp() {
        Clock fixedClock = Clock.fixed(Instant.ofEpochMilli(1234567890), ZoneId.systemDefault());
        SystemTimeService fixedTimeService = new SystemTimeService(fixedClock);
        fixedDateTime = fixedTimeService.asLocalDateTime();
    }


    @Test
    public void deserializeLocalDateTimeShouldBeInExpectedFormat() throws Exception {
        LocalDateTime expected = LocalDateTime.of(2017, 1, 10, 16, 8, 46);
        LocalDateTime actual = dateTimeParser.parse("\"2017-01-10T16:08:46\"").getObject();

        assertThat(actual, is(equalTo(expected)));
    }

    //TODO Missing nanosec
    @Test
    public void serializeLocalDateTimeShouldBeInExpectedFormat() throws Exception {
        String expected = fixedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS"));

        String actual = dateTimeParser.write(fixedDateTime).getJson();

        assertThat(actual, is(equalTo("\"" + expected + "\"")));
    }

//    private String asJsonObject(String value) throws IOException {
//        return "{ \"value\": \"" + value + "\"}";
//    }

//
//    public static final class ValueHolder {
//
//        private LocalDateTime value;
//
//        public ValueHolder() {
//        }
//
//        public ValueHolder(LocalDateTime value) {
//            this.value = value;
//        }
//
//        public LocalDateTime getValue() {
//            return value;
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (o == null || getClass() != o.getClass()) return false;
//
//            ValueHolder that = (ValueHolder) o;
//
//            return value != null ? value.equals(that.value) : that.value == null;
//        }
//
//        @Override
//        public int hashCode() {
//            return value != null ? value.hashCode() : 0;
//        }
//    }

}
