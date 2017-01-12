package de.kgeorgiew.carddb.json;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kgeorgiew
 */
public class JsonResult {

    private List<ClientError> errors = new ArrayList<>();

    public JsonResult() {
    }

    public void addError(ClientError fieldError) {
        errors.add(fieldError);
    }

    public void setErrors(List<ClientError> errors) {
        this.errors = errors;
    }

    public List<ClientError> getErrors() {
        return errors;
    }
}
