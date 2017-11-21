package com.currencycloud.auth.model.db;

import javax.persistence.*;

@Entity
@Table(name="accounts")
public class Account {
    @Id
    private String id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="id")
    private AccountCstm accountCstm;

    public AccountCstm getAccountCstm() {
        return accountCstm;
    }

    private String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
