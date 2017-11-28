package com.currencycloud.auth.events.auth;

import com.currencycloud.auth.model.db.ContactsCstm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.Date;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class LockContactSinkTest {

    private LockContactSink sink = mock(LockContactSink.class);

    private final ContactsCstm cstm = mock(ContactsCstm.class);

    @Test
    public void processFailedLoginEvent() throws Exception {

        FailedLogin login = new FailedLogin();

        login.setId("123");
        login.setLoginId("test@test.com");
        login.setReason(FailedLogin.Reason.INCORRECT_PASSWORD);

        given(sink.getContact(any(String.class))).willReturn(cstm);
        given(cstm.getFailedAttempts()).willCallRealMethod();

        when(sink.processFailedLoginEvent(any())).thenCallRealMethod();

        sink.processFailedLoginEvent(login);

        verify(cstm, times(1)).setLockReason("Grace login attempts exceeded");
        verify(cstm, times(1)).setLockedAt(any(Date.class));
        verify(cstm, times(1)).setLocked(true);
        verify(cstm, times(1)).setLastLoginAttempt(any(Date.class));

        verify(sink, times(1)).saveContact(cstm);

    }

}