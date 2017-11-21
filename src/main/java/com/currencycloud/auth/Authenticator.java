package com.currencycloud.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.currencycloud.auth")
public class Authenticator {

    public static void main(String[] args) {
        SpringApplication.run(Authenticator.class, args);
    }
}