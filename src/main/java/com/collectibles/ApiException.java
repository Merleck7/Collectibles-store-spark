package com.collectibles;

/**
 * Custom exception class for API error handling.
 */
public class ApiException extends RuntimeException {
    private int statusCode;

    public ApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

