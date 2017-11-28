package com.currencycloud.auth.events.auth;

import com.currencycloud.auth.model.db.ContactsCstm;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.datetime.DateFormatter;

import java.util.Date;

public class SuccessfulLogin {

    private String loginId;
    private String id;
    private Date time;

    public SuccessfulLogin() {

    }

    public SuccessfulLogin(ContactsCstm cstm, Date at) {
        DateFormatter fmt = new DateFormatter();
        fmt.setIso(DateTimeFormat.ISO.DATE_TIME);

        this.loginId = cstm.getLoginId();
        this.id = cstm.getId();
        this.time = at;
    }

    @Override
    public String toString() {
        return "SuccessfulLogin(" + loginId + ")";
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date at) {
        this.time = at;
    }

}
