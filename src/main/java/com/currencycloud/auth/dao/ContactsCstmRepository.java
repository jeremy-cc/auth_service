package com.currencycloud.auth.dao;

import com.currencycloud.auth.model.db.ContactsCstm;
import org.springframework.data.repository.CrudRepository;

// Data repository for user sessions
public interface ContactsCstmRepository extends CrudRepository<ContactsCstm, String>{

    ContactsCstm findByLoginId(String loginId);
}
