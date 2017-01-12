package de.kgeorgiew.carddb.json;

/**
 * @author kgeorgiew
 */
public class TextError implements ClientError {

    private String message;

    TextError() {
    }

    public TextError(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
