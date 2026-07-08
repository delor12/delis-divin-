package com.delisdivin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for Delis Divin SaaS platform.
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableJpaAuditing
public class DelisDivinApplication {

    public static void main(String[] args) {
        SpringApplication.run(DelisDivinApplication.class, args);
    }
}
