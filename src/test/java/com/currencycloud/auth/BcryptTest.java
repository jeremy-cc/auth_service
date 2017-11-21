package com.currencycloud.auth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class BcryptTest {

    @Test
    public void testGen10Generation() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        assertTrue(encoder.matches("my password",
                "$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa"));

        assertTrue(encoder.matches("my password",
                "$2a$14$9axC8akAGUXV0N4f/BMoleuCx8IU5zerF5s/m/gkXT4CraEJ5ywBS"));

    }


    @Test
    public void testRng() {
        Random r = new Random();
        IntStream stream = r.ints(100, 1, 4);

        stream.forEach(System.out::println);

    }
}
