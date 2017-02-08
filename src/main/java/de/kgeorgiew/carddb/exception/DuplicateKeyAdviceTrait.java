package de.kgeorgiew.carddb.exception;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.AdviceTrait;

import javax.ws.rs.core.Response;

/**
 * @author kgeorgiew
 *
 * @see DuplicateKeyException
 * @see Response.Status#CONFLICT
 */
public interface DuplicateKeyAdviceTrait extends AdviceTrait {

    @ExceptionHandler
    default ResponseEntity<Problem> handleDuplicateKeyException(
            final DuplicateKeyException exception,
            final NativeWebRequest request) {
        return create(Response.Status.CONFLICT, exception, request);
    }

}
