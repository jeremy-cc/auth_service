package com.currencycloud.auth.events.auth;

import com.currencycloud.auth.dao.ContactsCstmRepository;
import com.currencycloud.auth.model.db.ContactsCstm;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class FailedLoginSink {

    @Autowired
    private ContactsCstmRepository cstmRepository;

    @Value("${auth.maxattempts}")
    private int maxAttempts;

    @RabbitListener(queues = "failed_login")
    public boolean processFailedLoginEvent(@Payload FailedLogin message) {
        System.out.println("Processed failed login: << " + message + " >>" );

        ContactsCstm cstm = getContact(message.getId());

        cstm.setFailedAttempts(cstm.getFailedAttempts() + 1);
        saveContact(cstm);

        return true;
    }

    public ContactsCstm getContact(String id) {
        return cstmRepository.findOne(id);
    }

    public void saveContact(ContactsCstm cstm) {
        cstmRepository.save(cstm);
    }

}
