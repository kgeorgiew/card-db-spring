package de.kgeorgiew.carddb.json;

import de.kgeorgiew.carddb.domain.Lang;
import de.kgeorgiew.carddb.service.SystemTimeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.*;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 *
 *
 * @author kgeorgiew
 */
@RunWith(SpringRunner.class)
@JsonTest
public class JacksonConfigurationTest {

    @Autowired
    private JacksonTester<Lang> langConverter;

    @Test
    public void shouldIgnoreUnknownFields() throws Exception {
        String langJson = "{ \"lang\": \"ENG\", \"unknownField\": \"test\" }";

        Lang lang = langConverter.parseObject(langJson);

        assertThat(lang.getLang(), equalTo("ENG"));
        assertThat(lang.getCreated(), is(nullValue()));
        assertThat(lang.getCreatedBy(), is(nullValue()));
    }
}
