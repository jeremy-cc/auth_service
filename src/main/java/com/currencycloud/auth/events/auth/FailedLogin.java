package com.currencycloud.auth.events.auth;

import com.currencycloud.auth.model.db.Contact;
import com.currencycloud.auth.model.db.ContactsCstm;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.Date;
import java.util.Locale;

public class FailedLogin {

    private String loginId;
    private String id;
    private Date   failureTime;
    private Reason reason;

    public enum Reason {
        INCORRECT_PASSWORD("incorrect_password"),
        INCORRECT_SECURITY_ANSWER("incorrect_security_answer"),
        INCORRECT_API_KEY("incorrect_api_key");

        Reason(String reason) {
            this.reasonStr = reason;
        }

        final String reason() {
            return reasonStr;
        }

        private final String reasonStr;
    }

    public FailedLogin() {

    }

    public FailedLogin(ContactsCstm cstm, Date at, FailedLogin.Reason reason) {
        DateFormatter fmt = new DateFormatter();
        fmt.setIso(DateTimeFormat.ISO.DATE_TIME);

        this.loginId = cstm.getLoginId();
        this.id = cstm.getId();
        this.failureTime = at;
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "FailedLogin(" + loginId + ")";
    }


    // serialisation
    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getFailureTime() {
        return failureTime;
    }

    public void setFailureTime(Date failureTime) {
        this.failureTime = failureTime;
    }

    public Reason getReason() {
        return reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }
}
