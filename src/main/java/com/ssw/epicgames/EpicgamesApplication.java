package com.ssw.epicgames;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableCaching
public class EpicgamesApplication {
    public static void main(String[] args) {
        SpringApplication.run(EpicgamesApplication.class, args);
    }

}
