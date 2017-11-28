package com.currencycloud.auth.events.auth;

import com.currencycloud.auth.model.db.ContactsCstm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class FailedLoginSinkTest {

    private FailedLoginSink sink = mock(FailedLoginSink.class);

    private final ContactsCstm cstm = mock(ContactsCstm.class);

    @Test
    public void processFailedLoginEvent() throws Exception {

        FailedLogin login = new FailedLogin();

        login.setId("123");
        login.setLoginId("test@test.com");
        login.setReason(FailedLogin.Reason.INCORRECT_PASSWORD);

        given(sink.getContact("123")).willReturn(cstm);
        given(cstm.getFailedAttempts()).willCallRealMethod();

        when(sink.processFailedLoginEvent(any())).thenCallRealMethod();

        sink.processFailedLoginEvent(login);

        verify(cstm, times(1)).setFailedAttempts(any(Integer.class));
        verify(sink, times(1)).saveContact(cstm);

    }

}