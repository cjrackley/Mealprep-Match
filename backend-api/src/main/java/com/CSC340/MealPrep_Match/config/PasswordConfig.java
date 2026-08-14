package com.CSC340.MealPrep_Match.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Single shared password encoder. Customer and provider passwords must be hashed
 * with the same strength, so both services inject this bean rather than each
 * constructing their own {@link BCryptPasswordEncoder}.
 */
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
