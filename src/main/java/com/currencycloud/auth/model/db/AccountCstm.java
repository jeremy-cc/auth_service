package com.currencycloud.auth.model.db;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name="accounts_cstm")
public class AccountCstm {
    public static final String[] STATUS_ACTIVE = new String[]{
            "active",
            "active_with_limits",
            "active_1st_trade",
            "compliance_awaiting_for_update"
    };

    public static final String[] STATUS_COMPLIANCE_REQUESTED = new String[] {
            "compliance_rejected",
            "compliance_requested",
            "compliance_awaiting_for_update"
    };

    @Id
    @Column(name="id_c")
    private String id;

    @Column(name = "fxcg_account_id_c")
    private String fxcgAccountId;

    @Column(name = "online_session_duration_c")
    private int onlineSessionDuration;

    @Column(name = "fxcg_account_status_c")
    private String fxcgAccountStatus;

    @Column(name = "api_trading_enabled_c")
    private Boolean apiTradingEnabled;

    public String getId() {
        return id;
    }

    public String getFxcgAccountId() {
        return fxcgAccountId;
    }

    public int getOnlineSessionDuration() {
        return onlineSessionDuration;
    }

    public String getFxcgAccountStatus() {
        return fxcgAccountStatus;
    }

    public Boolean getApiTradingEnabled() {
        return apiTradingEnabled;
    }

    public boolean isActive() {
        List<String> myList = Arrays.asList(STATUS_ACTIVE);
        return myList.contains(getFxcgAccountStatus());
    }

    public boolean getComplianceRequested() {
        List<String> myList = Arrays.asList(STATUS_COMPLIANCE_REQUESTED);
        return myList.contains(getFxcgAccountStatus());
    }


}
