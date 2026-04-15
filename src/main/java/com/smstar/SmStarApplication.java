package com.smstar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.smstar.config.CorsProperties;

@SpringBootApplication
@EnableConfigurationProperties(CorsProperties.class)
public class SmStarApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmStarApplication.class, args);
    }
}
