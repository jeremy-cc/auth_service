package com.currencycloud.auth.events.auth;

import com.currencycloud.auth.dao.ContactsCstmRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class LockContactSink{
    @Autowired
    ContactsCstmRepository cstmRepository;

    @RabbitListener(queues = "lock_contact")
    public void processFailedLoginEvent(@Payload FailedLogin login) {
        System.out.println("Processed lock event: << " + login.toString() + " >>" );

//        ContactCstm cstm =
    }
}

