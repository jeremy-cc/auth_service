package com.currencycloud.auth.model;

public class Token {
    private final String authToken;

    public Token(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }
}
