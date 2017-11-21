package com.currencycloud.auth.model.db;

import com.currencycloud.auth.exception.DataAccessException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.FilterJoinTable;

import javax.persistence.*;
import java.util.Collection;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name="contacts")
public class Contact {

    @Id
    private String id;

    private String firstName;
    private String lastName;
    private String title;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id")
    private ContactsCstm contactCstm;

    @JoinTable(
            name = "accounts_contacts",
            joinColumns = @JoinColumn(table="contacts", name = "contact_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(table="accounts", name = "account_id", referencedColumnName = "id")
    )
    @ElementCollection
    @FilterJoinTable(name="notSoftDeleted", condition="deleted = false")
    private Collection<Account> accounts;

    public ContactsCstm getContactCstm() {
        return contactCstm;
    }

    public Account getAccount() throws DataAccessException {
        if (accounts.isEmpty()){
            throw new DataAccessException("Contact is not bound to an active account");
        }

        return accounts.iterator().next();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

