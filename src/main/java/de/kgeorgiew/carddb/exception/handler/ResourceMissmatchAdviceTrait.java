package de.kgeorgiew.carddb.exception.handler;

import de.kgeorgiew.carddb.exception.ResourceMissmatchException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.AdviceTrait;

import javax.ws.rs.core.Response;

/**
 * @author kgeorgiew
 */
public interface ResourceMissmatchAdviceTrait extends AdviceTrait {

    @ExceptionHandler
    default ResponseEntity<Problem> handleNotFoundException(
            final ResourceMissmatchException exception,
            final NativeWebRequest request) {

        return create(Response.Status.BAD_REQUEST, exception, request);
    }
}
