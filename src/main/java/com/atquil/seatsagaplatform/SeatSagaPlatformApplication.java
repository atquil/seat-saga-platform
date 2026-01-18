package com.atquil.seatsagaplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SeatSagaPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeatSagaPlatformApplication.class, args);
    }

}
