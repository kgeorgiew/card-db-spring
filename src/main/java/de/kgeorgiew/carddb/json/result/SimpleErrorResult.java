package de.kgeorgiew.carddb.json.result;

/**
 * @author kgeorgiew
 *
 * Wrapper for json response data
 *
 */
public class SimpleErrorResult implements JsonResult {

    private String message;

    public SimpleErrorResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
