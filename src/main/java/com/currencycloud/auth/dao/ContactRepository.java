package com.currencycloud.auth.dao;

import com.currencycloud.auth.model.db.Contact;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

// Data repository for user sessions
public interface ContactRepository extends CrudRepository<Contact, String>{

    @Query(value = "select c.* from contacts c inner join contacts_cstm cstm on c.id = cstm.id_c where cstm.login_id_c=:loginId", nativeQuery = true)
    Contact findByLoginId(@Param(value = "loginId") String loginId);
}
