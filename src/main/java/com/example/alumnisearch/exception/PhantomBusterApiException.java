package com.example.alumnisearch.exception;

public class PhantomBusterApiException extends RuntimeException {

    private final int statusCode;

    public PhantomBusterApiException(String message) {
        super(message);
        this.statusCode = 502;
    }

    public PhantomBusterApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 502;
    }

    public PhantomBusterApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
