package de.kgeorgiew.carddb.exception.handler;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.AdviceTrait;

import javax.ws.rs.core.Response;

/**
 * @author kgeorgiew
 */
public interface DatabaseExceptionHandler extends AdviceTrait {

    // TODO Handle all kind of DatabaseAccessException
    @ExceptionHandler
    default ResponseEntity<Problem> handleDataAccessException(
            final DataAccessException exception,
            final NativeWebRequest request) {

        return create(Response.Status.BAD_REQUEST, exception, request);
    }

    @ExceptionHandler
    default ResponseEntity<Problem> handleDuplicateKeyException(
            final DuplicateKeyException exception,
            final NativeWebRequest request) {
        return create(Response.Status.CONFLICT, exception, request);
    }
}
