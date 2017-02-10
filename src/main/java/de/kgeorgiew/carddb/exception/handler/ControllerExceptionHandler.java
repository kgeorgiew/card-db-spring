package de.kgeorgiew.carddb.exception.handler;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.spring.web.advice.ProblemHandling;

/**
 * @author kgeorgiew
 */
@ControllerAdvice
public class ControllerExceptionHandler implements ProblemHandling,
        DuplicateKeyAdviceTrait,
        NotFoundAdviceTrait {

}
