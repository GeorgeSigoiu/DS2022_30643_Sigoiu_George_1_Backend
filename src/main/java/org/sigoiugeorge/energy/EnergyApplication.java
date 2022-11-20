package org.sigoiugeorge.energy;

import org.sigoiugeorge.energy.service.impl.MessageQueueReceiver;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class EnergyApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnergyApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(MessageQueueReceiver mqr) {
        return args -> {
            mqr.start();
        };
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
