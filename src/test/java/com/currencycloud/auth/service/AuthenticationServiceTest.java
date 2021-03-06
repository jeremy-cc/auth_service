package com.currencycloud.auth.service;

import com.currencycloud.auth.events.auth.FailedLogin;
import com.currencycloud.auth.model.AuthenticationResponse;
import com.currencycloud.auth.model.db.Account;
import com.currencycloud.auth.model.db.AccountCstm;
import com.currencycloud.auth.model.db.Contact;
import com.currencycloud.auth.model.db.ContactsCstm;
import jdk.nashorn.internal.objects.annotations.Setter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
@WebAppConfiguration
@ComponentScan("com.currencycloud.auth")
@RunWith(SpringRunner.class)
public class AuthenticationServiceTest {

    private final Contact contact = mock(Contact.class);
    private final ContactsCstm cstm = mock(ContactsCstm.class);
    private final Account account = mock(Account.class);
    private final AccountCstm acstm = mock(AccountCstm.class);

    private final RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);

    @Test
    public void assertTradingEnabled() throws Exception {
        AuthenticationService authenticationService = mock(AuthenticationService.class);
        given(authenticationService.assertPermissionEnabled(any(), any(), any(), any())).willCallRealMethod();

        given(account.getAccountCstm()).willReturn(acstm);
        // verify isTradingEnabled checks contact locked status
        given(cstm.getTradingLoginEnabed()).willReturn("disabled");

        AuthenticationResponse response = authenticationService.assertPermissionEnabled(contact, cstm, account, "trading");

        assertFalse(response.canProceed());

        // verify isTradingEnabled checks account status
        given(cstm.isTradingEnabled()).willReturn(true);
        given(acstm.isActive()).willReturn(false);

        response = authenticationService.assertPermissionEnabled(contact, cstm, account, "trading");

        assertFalse(response.canProceed());

        given(acstm.isActive()).willReturn(true);
        response = authenticationService.assertPermissionEnabled(contact, cstm, account, "trading");

        assertTrue(response.canProceed());
    }

    @Test
    public void assertComplianceUploadEnabled() throws Exception {
        given(account.getAccountCstm()).willReturn(acstm);

        given(acstm.getFxcgAccountStatus()).willReturn("disabled");
        given(acstm.getComplianceRequested()).willCallRealMethod();
        assertFalse(acstm.getComplianceRequested());

        given(acstm.getFxcgAccountStatus()).willReturn("compliance_requested");
        assertEquals(acstm.getFxcgAccountStatus(), "compliance_requested");
        assertTrue(acstm.getComplianceRequested());
    }

    @Test
    public void assertIncorrectPasswordBlocksAuthentication() throws Exception {
        when(cstm.assertPasswordHashMatches(any(String.class))).thenReturn(false);
        AuthenticationService authenticationService = mock(AuthenticationService.class);
        given(authenticationService.assertPasswordCorrect(any(), any(), any())).willCallRealMethod();

        given(authenticationService.getRabbitTemplate()).willReturn(rabbitTemplate);

        // check that if password check is successful, auth can proceed to next step
        AuthenticationResponse response =  authenticationService.assertPasswordCorrect("testpassword", contact, cstm);
        assertTrue(response.isFailed());
        assertEquals(response.getResponseData().get("error_code"), "invalid_supplied_credentials");

        verify(cstm, times(1)).assertPasswordHashMatches(any(String.class));
    }

    @Test
    public void assertPasswordCorrectPassesAuthentication() throws Exception {
        given(cstm.assertPasswordHashMatches(any(String.class))).willReturn(true);
        AuthenticationService authenticationService = mock(AuthenticationService.class);
        given(authenticationService.assertPasswordCorrect(any(), any(), any())).willCallRealMethod();

        // check that if password check is successful, auth can proceed to next step
        assertTrue(authenticationService.assertPasswordCorrect("testpassword", contact, cstm).canProceed());
        verify(cstm, times(1)).assertPasswordHashMatches(any(String.class));
    }

    @Test
    public void assertSecurityQuestionBlocksAuthentication() throws Exception {
        AuthenticationService authenticationService = mock(AuthenticationService.class);
        given(authenticationService.assertSecurityQuestionCorrect(1, "String1", cstm)).willCallRealMethod();
        given(authenticationService.getRabbitTemplate()).willReturn(rabbitTemplate);

        given(contact.getId()).willReturn("test uuid");
        when(cstm.assertSecurityAnswer(eq(1), any(String.class))).thenReturn(false);

        AuthenticationResponse response = authenticationService.assertSecurityQuestionCorrect(1, "String1", cstm);
        assertTrue(response.isFailed());
        assertEquals(response.getResponseData().get("error_code"), "invalid_supplied_credentials");

        verify(rabbitTemplate, times(1)).convertAndSend(any(String.class), any(String.class), any(FailedLogin.class));
        verify(cstm, times(1)).assertSecurityAnswer(eq(1), any(String.class));
    }

    @Test
    public void assertSecurityQuestionPassesAuthentication() throws Exception {
        AuthenticationService authenticationService = mock(AuthenticationService.class);
        when(cstm.assertSecurityAnswer(eq(1), any(String.class))).thenReturn(true);

        given(authenticationService.assertSecurityQuestionCorrect(1, "String1", cstm)).willCallRealMethod();

        AuthenticationResponse response = authenticationService.assertSecurityQuestionCorrect(1, "String1", cstm);
        assertTrue(response.canProceed());

        verify(cstm, times(1)).assertSecurityAnswer(eq(1), any(String.class));
    }
}