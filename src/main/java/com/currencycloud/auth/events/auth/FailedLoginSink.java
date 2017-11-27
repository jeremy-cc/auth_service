package com.currencycloud.auth.events.auth;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class FailedLoginSink {

    @RabbitListener(queues = "failed_login")
    public void processFailedLoginEvent(@Payload FailedLogin message) {
        System.out.println("Processed failed login: << " + message + " >>" );
    }

}
