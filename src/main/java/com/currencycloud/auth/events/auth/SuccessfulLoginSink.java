package com.currencycloud.auth.events.auth;

import com.currencycloud.auth.dao.ContactsCstmRepository;
import com.currencycloud.auth.model.db.ContactsCstm;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class SuccessfulLoginSink {

    @Autowired
    ContactsCstmRepository cstmRepository;

    @Value("${auth.maxattempts}")
    private int maxAttempts;

    @RabbitListener(queues = "successful_login")
    public void processFailedLoginEvent(@Payload SuccessfulLogin message) {
        // guard against

        System.out.println("Processed sucessful login: << " + message + " >>" );

        ContactsCstm cstm = getContact(message.getId());

        if(cstm.getFailedAttempts() < maxAttempts) {
            System.out.println("Resetting lock flags on contact");
            cstm.setFailedAttempts(0);
            cstm.setLastLoginAttempt(message.getTime());
            cstm.setLastSuccessfulAttempt(message.getTime());
            cstm.setLocked(false);
            cstm.setLockedAt(null);
            cstm.setLockReason(null);

            saveContact(cstm);
        } else {
            System.out.println("Contact authed successfully but is over max failed attempts - raise for investigation.");
        }
    }

    public ContactsCstm getContact(String id) {
        return cstmRepository.findOne(id);
    }

    public void saveContact(ContactsCstm cstm) {
        cstmRepository.save(cstm);
    }
}
