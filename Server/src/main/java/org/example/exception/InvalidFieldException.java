package org.example.exception;

public class InvalidFieldException extends Exception implements HttpRequestException {
    private final int statusCode;

    public InvalidFieldException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }
}
