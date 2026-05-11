package com.example.urlshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

//Spring Boot starts embedded server, scans for components, creates beans, and wires dependencies automatically.
// @SpringBootApplication
// This is actually 3 annotations combined:
// @Configuration
// @EnableAutoConfiguration
// @ComponentScan

@SpringBootApplication
@EnableJpaAuditing
public class UrlshortenerApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrlshortenerApplication.class, args);
    }
}