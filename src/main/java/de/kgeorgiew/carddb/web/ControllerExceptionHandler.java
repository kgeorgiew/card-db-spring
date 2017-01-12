package de.kgeorgiew.carddb.web;

import de.kgeorgiew.carddb.json.ClientError;
import de.kgeorgiew.carddb.json.JsonResult;
import de.kgeorgiew.carddb.json.TextError;
import de.kgeorgiew.carddb.json.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author kgeorgiew
 */
@ControllerAdvice(annotations = {RestController.class})
public class ControllerExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<JsonResult> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        JsonResult jsonResult = new JsonResult();

        List<ClientError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new ValidationError(fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.toList());

        jsonResult.setErrors(errors);

        return new ResponseEntity<>(jsonResult, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    private ResponseEntity<JsonResult> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        JsonResult jsonResult = new JsonResult();
        String msg = messageSource.getMessage("error.request.body.invalid", null, Locale.getDefault());
        jsonResult.addError(new TextError(msg));

        return new ResponseEntity<>(jsonResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    private ResponseEntity<JsonResult> handleDuplicateKey(DuplicateKeyException ex) {
        JsonResult jsonResult = new JsonResult();
        String msg = messageSource.getMessage("error.entity.duplicateEntry", null, Locale.getDefault());
        jsonResult.addError(new TextError(msg));

        return new ResponseEntity<>(jsonResult, HttpStatus.CONFLICT);
    }

}
