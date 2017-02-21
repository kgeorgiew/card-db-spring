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
        String actualValue = "";
        String expectedError = "length must be exact 3";

        assertInvalid(actualValue, expectedError);
    }

    @Test
    public void shouldRejectFieldValueLongerThan3Chars() throws Exception {
        String actualValue = "Im too long";
        String expectedError = "length must be exact 3";

        assertInvalid(actualValue, expectedError);
    }

    @Test
    public void shouldRejectNullFieldValue() throws Exception {
        String actualValue = null;
        String expectedError = "may not be null";

        assertInvalid(actualValue, expectedError);
    }

    private void assertInvalid(String fieldValue, String expectedMessage) {
        Lang inputValue = new Lang(fieldValue, "admin", ZonedDateTime.now());

        Set<ConstraintViolation<Lang>> validations = validator.validate(inputValue);
        validations.forEach( constraint ->
                assertThat(constraint.getMessage(), equalTo(expectedMessage))
        );
    }
}
