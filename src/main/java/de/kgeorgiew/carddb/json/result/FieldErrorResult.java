package de.kgeorgiew.carddb.json.result;

import de.kgeorgiew.carddb.json.ClientMessage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kgeorgiew
 *
 * Wrapper for json response data
 *
 */
public class FieldErrorResult extends SimpleErrorResult {

    private List<ClientMessage> errors = new ArrayList<>();

    public FieldErrorResult(String message) {
        super(message);
    }

    public FieldErrorResult addError(ClientMessage fieldError) {
        errors.add(fieldError);
        return this;
    }

    public FieldErrorResult setErrors(List<ClientMessage> errors) {
        this.errors = errors;
        return this;
    }

    public List<ClientMessage> getErrors() {
        return errors;
    }

}
