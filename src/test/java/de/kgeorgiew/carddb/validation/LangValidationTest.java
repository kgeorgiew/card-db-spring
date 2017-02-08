package de.kgeorgiew.carddb.validation;

import de.kgeorgiew.carddb.domain.Lang;
import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.time.ZonedDateTime;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author kgeorgiew
 */
public class LangValidationTest {

    private LocalValidatorFactoryBean validator;

    @Before
    public void setup() {
        validator = new LocalValidatorFactoryBean();
        validator.setProviderClass(HibernateValidator.class);
        validator.afterPropertiesSet();
    }

    @Test
    public void shouldRejectFieldValueThan3Chars() throws Exception {
        String fieldValue = "";

        String expectedMessage = "length must be exact 3";

        assertInvalid(expectedMessage, fieldValue);
    }

    @Test
    public void shouldRejectFieldValueLongerThan3Chars() throws Exception {
        String fieldValue = "Im too long";

        String expectedMessage = "length must be exact 3";

        assertInvalid(expectedMessage, fieldValue);
    }

    @Test
    public void shouldRejectNullFieldValue() throws Exception {
        String fieldValue = null;
        String expectedMessage = "may not be null";

        assertInvalid(expectedMessage, fieldValue);
    }

    private void assertInvalid(String expectedMessage, String fieldValue) {
        Lang inputValue = new Lang(fieldValue, "admin", ZonedDateTime.now());

        Set<ConstraintViolation<Lang>> validations = validator.validate(inputValue);
        validations.forEach( constraint ->
                assertThat(constraint.getMessage(), equalTo(expectedMessage))
        );
    }
}
