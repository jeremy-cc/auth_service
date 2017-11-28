package com.currencycloud.auth.model.db;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.persistence.*;
import java.util.Date;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "contacts_cstm")
public class ContactsCstm {

    @Id
    @Column(name="id_c")
    private String id;

    @OneToOne(mappedBy = "contactCstm")
    private Contact contact;

    @Column(name="login_id_c")
    private String loginId;
    private Boolean locked;
    private Date lockedAt;
    private String lockReason;
    private Date lastLoginAttempt;
    private Date lastSuccessfulAttempt;

    @Column(name="back_office_login_enabled_c")
    private boolean backOfficeLoginEnabled;

    @Column(name="trading_login_enabled_c")
    private String tradingLoginEnabed;

    @Column(name="broker_permissions_c")
    private String brokerPermissions;

    @Column(name="password_c")
    private String passwordHash;

    @Column(name="api_key_c")
    private String apiKey;

    @Column(name="encrypted_security_answer_1_c")
    private String securityAnswer1;
    @Column(name="encrypted_security_answer_2_c")
    private String securityAnswer2;
    @Column(name="encrypted_security_answer_3_c")
    private String securityAnswer3;

    @Column(name="security_question_1_c")
    private String securityQuestion1;
    @Column(name="security_question_2_c")
    private String securityQuestion2;
    @Column(name="security_question_3_c")
    private String securityQuestion3;

    @Column(name="failed_attempts")
    private int failedAttempts;

    public String getId() {
        return id;
    }

    public String getLoginId() {
        return loginId;
    }

    public Boolean isLocked() {
        return locked;
    }

    public Date getLockedAt() {
        return lockedAt;
    }

    public String getLockReason() {
        return lockReason;
    }

    public Date getLastLoginAttempt() {
        return lastLoginAttempt;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public Date getLastSuccessfulAttempt() {
        return lastSuccessfulAttempt;
    }

    public boolean isBackOfficeLoginEnabled() {
        return backOfficeLoginEnabled;
    }

    public String getTradingLoginEnabed() {
        return tradingLoginEnabed;
    }

    public boolean isTradingEnabled() {
        return getTradingLoginEnabed().equals("enabled");
    }

    public boolean assertPasswordHashMatches(String hash) {
        return BCrypt.checkpw(hash, passwordHash);
    }

    public boolean assertApiKeyMatches(String hash) {
        return BCrypt.checkpw(hash, apiKey);
    }

    public String getSecurityAnswer1() {
        return securityAnswer1;
    }

    public String getSecurityAnswer2() {
        return securityAnswer2;
    }

    public String getSecurityAnswer3() {
        return securityAnswer3;
    }

    public String getBrokerPermissions() {
        return brokerPermissions;
    }

    public Contact getContact() {
        return contact;
    }

    public boolean assertSecurityAnswer(int idx, String answer) {
        switch(idx) {
            case 1:
                return BCrypt.checkpw(answer, getSecurityAnswer1());
            case 2:
                return BCrypt.checkpw(answer, getSecurityAnswer2());
            case 3:
                return BCrypt.checkpw(answer, getSecurityAnswer3());
            default:
                return false;
        }
    }

    public String getSecurityQuestion1() {
        return securityQuestion1;
    }

    public String getSecurityQuestion2() {
        return securityQuestion2;
    }

    public String getSecurityQuestion3() {
        return securityQuestion3;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public void setLockedAt(Date lockedAt) {
        this.lockedAt = lockedAt;
    }

    public void setLockReason(String lockReason) {
        this.lockReason = lockReason;
    }

    public void setLastLoginAttempt(Date lastLoginAttempt) {
        this.lastLoginAttempt = lastLoginAttempt;
    }

    public void setLastSuccessfulAttempt(Date lastSuccessfulAttempt) {
        this.lastSuccessfulAttempt = lastSuccessfulAttempt;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }
}

