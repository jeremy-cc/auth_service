package com.currencycloud.auth.dao;

import com.currencycloud.auth.model.db.ContactsCstm;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

// Data repository for user sessions
public interface ContactsCstmRepository extends CrudRepository<ContactsCstm, String>{


    @Cacheable("contactscstm")
    ContactsCstm findOne(String id);

    @Cacheable("contactscstm")
    ContactsCstm findByLoginId(String loginId);
}
