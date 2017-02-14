package de.kgeorgiew.carddb.exception;

/**
 *
 * Exception to be thrown if an path variable and enity key are different.
 *
 * @author kgeorgiew
 */
public class ResourceMissmatchException extends RuntimeException {

    public ResourceMissmatchException() {
    }

    public ResourceMissmatchException(String message) {
        super(message);
    }

    public ResourceMissmatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceMissmatchException(Throwable cause) {
        super(cause);
    }

    public ResourceMissmatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
