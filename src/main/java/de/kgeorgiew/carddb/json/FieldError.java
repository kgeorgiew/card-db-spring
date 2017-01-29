package de.kgeorgiew.carddb.json;

/**
 * @author kgeorgiew
 */
public class FieldError implements ClientMessage {

    private String field;
    private String message;

    FieldError() {
    }

    public FieldError(String field, String message) {
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
