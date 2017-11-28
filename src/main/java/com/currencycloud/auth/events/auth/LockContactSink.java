package com.currencycloud.auth.events.auth;

import com.currencycloud.auth.dao.ContactsCstmRepository;
import com.currencycloud.auth.model.db.ContactsCstm;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class LockContactSink{
    @Autowired
    ContactsCstmRepository cstmRepository;

    @Value("${auth.maxattempts}")
    private int maxAttempts;

    @RabbitListener(queues = "lock_contact")
    public boolean processFailedLoginEvent(@Payload FailedLogin login) {
        System.out.println("Processed lock event: << " + login.toString() + " >>" );

        ContactsCstm cstm = getContact(login);

        if(cstm.getFailedAttempts() + 1 >= maxAttempts) {
            System.out.println("Contact has exceeded grace attempts; locking");

            cstm.setLockReason("Grace login attempts exceeded");
            cstm.setLockedAt(Calendar.getInstance().getTime());
            cstm.setLocked(true);
            cstm.setLastLoginAttempt(Calendar.getInstance().getTime());

            saveContact(cstm);
        }

        return true;
    }

    public void saveContact(ContactsCstm cstm) {
        cstmRepository.save(cstm);
    }

    public ContactsCstm getContact(@Payload FailedLogin login) {
        return cstmRepository.findOne(login.getId());
    }
}

