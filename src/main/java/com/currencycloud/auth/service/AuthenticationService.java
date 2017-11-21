package com.currencycloud.auth.service;

import com.currencycloud.auth.dao.ContactsCstmRepository;
import com.currencycloud.auth.exception.DataAccessException;
import com.currencycloud.auth.model.AuthenticationResponse;
import com.currencycloud.auth.model.db.Account;
import com.currencycloud.auth.model.db.AccountCstm;
import com.currencycloud.auth.model.db.Contact;
import com.currencycloud.auth.model.db.ContactsCstm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class AuthenticationService {

    public static final String DENIED_INVALID_SUPPLIED_CREDENTIALS = "invalid_supplied_credentials";
    public static final String DENIED_CONTACT_LOCKED = "contact_locked";
    public static final String DENIED_CONTACT_NOT_PERMISSIONED = "contact_not_permissioned";
    public static final String DENIED_ACCOUNT_INACTIVE = "account_inactive";

    public static final String[] SECURITY_QUESTIONS = {
            "sec_q_fav_animal",
            "sec_q_fav_city",
            "sec_q_hol_dst",
            "sec_q_pet_name"
    };

    @Autowired
    private ContactsCstmRepository cstmRepository;

    public AuthenticationService(){

    }

    /*
    * Attempt to authenticate against the db
    */
    public Map<String,Object> authenticate(String loginId, String password, int q_no, String answer, String permission) throws DataAccessException {
        ContactsCstm cstm = cstmRepository.findByLoginId(loginId);

        if(null == cstm) {
            return generateFailedResponse(loginId, DENIED_INVALID_SUPPLIED_CREDENTIALS, "Access denied, incorrect credentials")
                    .getResponseData();
        }

        Contact contact = cstm.getContact();

        // verify the contact is not locked
        if(cstm.isLocked()) {
            return generateFailedResponse(loginId, DENIED_CONTACT_LOCKED, "Access denied, contact locked")
                    .getResponseData();
        }

        Account account = contact.getAccount();

        // Check permissions for the contact
        // trading or compliance upload must be enabled
        AuthenticationResponse response =  assertPermissionEnabled(contact, cstm, account, "trading");
        AuthenticationResponse complianceResponse = null;
        if(response.isFailed()) {
            // trading is not enabled
            complianceResponse = assertComplianceUploadEnabled(cstm, account);
            if(complianceResponse.isFailed()) {
                // trading is not enabled and this account is not in a compliance processing pre-enabled state therefore abort.
                return response.getResponseData();
            }
        }

        response = assertPasswordCorrect(password, contact, cstm);
        // Verify the password is correct
        if(response.isFailed()) {
            return response.getResponseData();
        }

        // Check the security answer is correct
        response = assertSecurityQuestionCorrect(q_no, answer, cstm);
        if(response.isFailed()) {
            return response.getResponseData();
        }

        // create successful authentication hash response
        return generateSuccessfulResponse(contact, cstm, account).getResponseData();
    }


    public Map<String,Object> authenticateApi(String loginId, String apiKey) throws DataAccessException{
        ContactsCstm cstm = cstmRepository.findByLoginId(loginId);
        if(null == cstm) {
            return generateFailedResponse(loginId, DENIED_INVALID_SUPPLIED_CREDENTIALS, "Access denied, incorrect credentials")
                    .getResponseData();
        }

        Contact contact = cstm.getContact();

        if(cstm.isLocked()) {
            return generateFailedResponse(loginId, DENIED_CONTACT_LOCKED, "Access denied, contact locked")
                    .getResponseData();
        }

        Account account = contact.getAccount();
        // Check permissions for the account - api trading must be enabled
        AuthenticationResponse response =  assertAccountApiTradingEnabled(cstm, account);
        if(response.isFailed()) {
            // trading is not enabled
            return response.getResponseData();
        }

        // Check permissions for the contact
        // trading or compliance upload must be enabled
        response =  assertPermissionEnabled(contact, cstm, account, "trading");
        if(response.isFailed()) {
            // trading is not enabled
            return response.getResponseData();
        }

        response = assertApiKeyCorrect(apiKey, cstm);
        if(response.isFailed()) {
            return response.getResponseData();
        }

        // create successful authentication hash response
        return generateSuccessfulResponse(contact, cstm, account).getResponseData();
    }

    // get security question details
    public Map<String,Object> getSecurityQuestion(String loginId) {
        ContactsCstm cstm = cstmRepository.findByLoginId(loginId);

        if(null == cstm) {
            return generateFailedResponse(loginId, DENIED_INVALID_SUPPLIED_CREDENTIALS, "Access denied, incorrect credentials")
                    .getResponseData();
        }

        // generate a random number in the range 1 : 4
        Random r = new Random();
        int questionNumber = r.ints(1, 1, 4).findFirst().getAsInt();

        String question = "";
        switch(questionNumber) {
            case 1:
                question = cstm.getSecurityQuestion1();
                break;
            case 2:
                question = cstm.getSecurityQuestion2();
                break;
            case 3:
                question = cstm.getSecurityQuestion3();
                break;
            default:
                return generateFailedResponse(loginId, DENIED_INVALID_SUPPLIED_CREDENTIALS, "Access denied, incorrect credentials")
                        .getResponseData();
        }

        Map<String, Object> result = new HashMap<>();

        result.put("login_id", loginId);
        result.put("security_question_number", questionNumber);
        result.put("security_question", question);
        result.put("failed_login_attempts", cstm.getFailedAttempts());

        return result;
    }

    // get apiproxy_authorisation_details
    public Map<String,Object> getApiProxyAuthorisationDetails(String loginId) throws DataAccessException {
        ContactsCstm cstm = cstmRepository.findByLoginId(loginId);

        if(null == cstm) {
            return generateFailedResponse(loginId, DENIED_INVALID_SUPPLIED_CREDENTIALS, "Access denied, incorrect credentials")
                    .getResponseData();
        }

        Contact contact = cstm.getContact();
        Account account = contact.getAccount();

        return generateSuccessfulResponse(contact, cstm, account).getResponseData();
    }


    private AuthenticationResponse generateFailedResponse(String loginId, String code, String reason) {
        Map<String,Object> body = new HashMap<>();

        body.put("login_id", loginId);
        body.put("passed", false);
        body.put("reason", reason);
        body.put("error_code", code);

        return AuthenticationResponse.failed(body);
    }

    private AuthenticationResponse generateSuccessfulResponse(Contact contact, ContactsCstm cstm, Account account) {
        Map<String, Object> body = new HashMap<>();

        AccountCstm acstm = account.getAccountCstm();

        body.put("login_id", cstm.getLoginId());
        body.put("passed", true);
        body.put("rfx_ref", account.getAccountCstm().getFxcgAccountId());
        body.put("contact_id", contact.getId());
        body.put("account_id", account.getId());
        body.put("account_name", account.getName());
        body.put("first_name", contact.getFirstName());
        body.put("last_name", contact.getLastName());
        body.put("session_duration", acstm.getOnlineSessionDuration());
        body.put("broker", cstm.getBrokerPermissions());

        return AuthenticationResponse.successful(body);
    }

    public AuthenticationResponse assertPermissionEnabled(Contact contact, ContactsCstm cstm, Account account, String permission)  {
        switch(permission) {
            case "trading":
                if (!cstm.isTradingEnabled()) {
                    return generateFailedResponse(cstm.getLoginId(), DENIED_CONTACT_NOT_PERMISSIONED, "Contact is not enabled for trading");
                }
                break;
            case "back_office":
                if (!cstm.isBackOfficeLoginEnabled()) {
                    return generateFailedResponse(cstm.getLoginId(), DENIED_CONTACT_NOT_PERMISSIONED, "Contact is not enabled for back_office");
                }
                break;
            default:
                return generateFailedResponse(cstm.getLoginId(), DENIED_CONTACT_NOT_PERMISSIONED, "Contact lacks the required permissions");
        }
        if(!account.getAccountCstm().isActive()) {
            return generateFailedResponse(cstm.getLoginId(), DENIED_CONTACT_NOT_PERMISSIONED, "Account is not active");
        }

        return AuthenticationResponse.pass();
    }

    /*
    * Verify that this account is active and that the contact is enabled for trading.
    * */
    public AuthenticationResponse assertAccountApiTradingEnabled(ContactsCstm cstm, Account account)  {
        if(!account.getAccountCstm().getApiTradingEnabled()) {
            return generateFailedResponse(cstm.getLoginId(), DENIED_INVALID_SUPPLIED_CREDENTIALS, "Access denied, incorrect credentials");
        }
        return AuthenticationResponse.pass();
    }

    /*
    *   Verify the account is in a compliance check status prior to being made active
    */
    public AuthenticationResponse assertComplianceUploadEnabled(ContactsCstm cstm, Account account) {
        if(!account.getAccountCstm().getComplianceRequested()) {
            return generateFailedResponse(cstm.getLoginId(), DENIED_CONTACT_NOT_PERMISSIONED, "Contact is not enabled for trading");
        }

        return AuthenticationResponse.pass();
    }

    /*
    * Verify that the bcrypted hash of the password and salt matches what we have in the database
    * */
    public AuthenticationResponse assertPasswordCorrect(String password, Contact contact, ContactsCstm cstm) {
        if(!cstm.assertPasswordHashMatches(password)) {
            return generateFailedResponse(cstm.getLoginId(), DENIED_INVALID_SUPPLIED_CREDENTIALS, "Access denied, incorrect credentials");
        }

        return AuthenticationResponse.pass();
    }

    public AuthenticationResponse assertApiKeyCorrect(String apiKey, ContactsCstm cstm) {
        if(!cstm.assertApiKeyMatches(apiKey)) {
            return generateFailedResponse(cstm.getLoginId(), DENIED_INVALID_SUPPLIED_CREDENTIALS, "Access denied, incorrect credentials");
        }

        return AuthenticationResponse.pass();
    }

    /*
    * Verify that the bcrypted security answer matches what we have in our database
    */
    public AuthenticationResponse assertSecurityQuestionCorrect(int questionNumber, String answer,  ContactsCstm cstm) {
        if(!cstm.assertSecurityAnswer(questionNumber, answer)) {
            return generateFailedResponse(cstm.getLoginId(), DENIED_INVALID_SUPPLIED_CREDENTIALS, "Access denied, incorrect credentials");
        }

        return AuthenticationResponse.pass();
    }


}
