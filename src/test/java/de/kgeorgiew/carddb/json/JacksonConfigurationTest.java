package de.kgeorgiew.carddb.json;

import de.kgeorgiew.carddb.domain.Lang;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
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

        Lang result = langConverter.parseObject(langJson);

        assertThat(result, is(notNullValue()));
    }
}
