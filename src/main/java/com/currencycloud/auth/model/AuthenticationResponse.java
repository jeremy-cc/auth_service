package com.currencycloud.auth.model;

import java.util.Map;

public class AuthenticationResponse {
    private final Status status;
    private final Map<String,Object> responseData;

    public AuthenticationResponse(Status status, Map<String,Object> body) {
        this.status = status;
        this.responseData = body;
    }

    public static AuthenticationResponse failed(Map<String,Object> body) {
        return new AuthenticationResponse(Status.FAILED, body);
    }

    public static AuthenticationResponse pass() {
        return new AuthenticationResponse(Status.CHECK_PASSED, null);
    }

    public static AuthenticationResponse successful(Map<String,Object> body) {
        return new AuthenticationResponse(Status.SUCCESSFUL, body);
    }

    public Map<String, Object> getResponseData() {
        return responseData;
    }

    public boolean isSuccesful() {
        return this.status == Status.SUCCESSFUL;
    }

    public boolean isFailed() {
        return this.status == Status.FAILED;
    }

    public boolean canProceed() {
        return this.status == Status.CHECK_PASSED;
    }

    public enum Status {
        SUCCESSFUL,
        CHECK_PASSED,
        FAILED
    }
}
