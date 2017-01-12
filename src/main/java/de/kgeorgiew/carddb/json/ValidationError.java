package de.kgeorgiew.carddb.json;

/**
 * @author kgeorgiew
 */
public class ValidationError implements ClientError {

    private String field;
    private String message;

    ValidationError() {
    }

    public ValidationError(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
