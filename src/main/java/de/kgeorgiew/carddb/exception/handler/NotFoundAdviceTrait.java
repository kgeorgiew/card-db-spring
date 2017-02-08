package de.kgeorgiew.carddb.exception.handler;

import de.kgeorgiew.carddb.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.AdviceTrait;

import javax.ws.rs.core.Response;

/**
 * @author kgeorgiew
 */
public interface NotFoundAdviceTrait extends AdviceTrait {


    @ExceptionHandler
    default ResponseEntity<Problem> handleNotFoundException(
            final ResourceNotFoundException exception,
            final NativeWebRequest request) {

        return create(Response.Status.NOT_FOUND, exception, request);
    }
}
