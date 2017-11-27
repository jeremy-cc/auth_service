package com.currencycloud.auth.events;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpRegistry {
    public static final String EXCHANGE_NAME_AUTHENTICATION = "auth";

    @Bean
    public Queue getFailedLoginQueue() {
        return new Queue("failed_login");
    }

    @Bean
    public Queue getLockContactEventQueue() {
        return new Queue("lock_contact");
    }

    @Bean
    public TopicExchange getAuthExchange() {
        return new TopicExchange(EXCHANGE_NAME_AUTHENTICATION);
    }

    @Bean
    public Binding getFailedLoginBinding() {
        return BindingBuilder.bind(getFailedLoginQueue()).to(getAuthExchange()).with("auth.failed.#");
    }

    @Bean
    public Binding getLockContactBinding() {
        return BindingBuilder.bind(getLockContactEventQueue()).to(getAuthExchange()).with("auth.failed.#");
    }

//    @Autowired
//    private ConnectionFactory connectionFactory;
//
//    // base level configuration
//    @Bean
//    public RabbitTemplate rabbitTemplate() {
//        RabbitTemplate template = new RabbitTemplate(connectionFactory);
//        template.setMessageConverter(jsonMessageConverter());
//        configureRabbitTemplate(template);
//        return template;
//    }
//
//    @Bean
//    public MessageConverter jsonMessageConverter() {
//        return new Jackson2JsonMessageConverter();
//    }

}
