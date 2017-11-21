package com.currencycloud.auth.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class AuthenticationResponseTest {
    @Test
    public void failed() throws Exception {
        AuthenticationResponse response = AuthenticationResponse.failed(null);

        assertTrue(response.isFailed());
    }

    @Test
    public void pass() throws Exception {
        AuthenticationResponse response = AuthenticationResponse.pass();

        assertTrue(response.canProceed());
    }

    @Test
    public void successful() throws Exception {
        AuthenticationResponse response = AuthenticationResponse.successful(null);

        assertTrue(response.isSuccesful());
    }
}