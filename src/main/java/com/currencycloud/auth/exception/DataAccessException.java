package com.currencycloud.auth.exception;

public class DataAccessException extends Exception {
    private final String message;

    public DataAccessException(String message) {
        this.message = message;
    }

    public String toString() {
        return message;
    }

}
