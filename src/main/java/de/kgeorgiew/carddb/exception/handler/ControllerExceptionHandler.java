package de.kgeorgiew.carddb.exception.handler;

import de.kgeorgiew.carddb.exception.ResourceNotFoundException;
import de.kgeorgiew.carddb.exception.handler.DuplicateKeyAdviceTrait;
import de.kgeorgiew.carddb.exception.handler.NotFoundAdviceTrait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.ProblemHandling;

/**
 * @author kgeorgiew
 */
@ControllerAdvice
public class ControllerExceptionHandler implements ProblemHandling, DuplicateKeyAdviceTrait, NotFoundAdviceTrait {

    //    @Autowired
//    private MessageSource messageSource;
//
//
//    //TODO handle org.springframework.web.bind
////        MethodArgumentNotValidException
////        MissingPathVariableException
////        MissingServletRequestParameterException
////        ServletRequestBindingException
////        UnsatisfiedServletRequestParameterException
//
//
////    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
////    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE) // 422
////    @ResponseBody
////    private Resource<JsonResult> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex) {
////
////        return new Resource<>(new SimpleErrorResult(msg));
////    }
//
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY) // 422
//    @ResponseBody
//    private Resource<JsonResult> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
//        String msg = messageSource.getMessage("error.validation", null, request.getLocale());
//        List<ClientMessage> errors = ex.getBindingResult()
//                .getFieldErrors()
//                .stream()
//                .map(fe -> new FieldError(fe.getField(), fe.getDefaultMessage()))
//                .collect(Collectors.toList());
//        // TODO Return VndErrors with description and help link?
//        return new Resource<>(new FieldErrorResult(msg).setErrors(errors));
//    }
//
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
//    @ResponseBody
//    private Resource<JsonResult> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
//        String msg = messageSource.getMessage("error.request.body.invalid", null, request.getLocale());
//
//        if(ex.contains(InvalidFormatException.class)) {
//            Throwable t = ex.getCause();
//            while(t != null) {
//                if(t instanceof InvalidFormatException) {
//                    //TODO Add col and line number?
//                    InvalidFormatException ife = (InvalidFormatException) ex.getMostSpecificCause();
////                    int col = ife.getLocation().getColumnNr();
////                    int line = ife.getLocation().getLineNr();
//
//                    String fieldName = ife.getPath()
//                            .stream()
//                            .map(f -> f.getFieldName())
//                            .collect(Collectors.joining("."));
//
//                    //TODO Test if fieldName is empty/null;
//                    FieldErrorResult result = new FieldErrorResult(msg)
//                        .addError(new FieldError(fieldName, ife.getMessage()));
//                    return new Resource<>(result);
//                }
//                t = t.getCause();
//            }
//        }
//
//        return new Resource<>(new SimpleErrorResult(msg));
//    }
//
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.CONFLICT) // 409
//    @ResponseBody
//    private Resource<JsonResult> handleDuplicateKey(DuplicateKeyException ex, WebRequest request) {
//        String msg = messageSource.getMessage("error.entity.duplicateEntry", null, request.getLocale());
//        return new Resource<>( new SimpleErrorResult(msg));
//    }

}
