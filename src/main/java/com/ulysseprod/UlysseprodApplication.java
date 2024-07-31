package com.ulysseprod;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class UlysseprodApplication {

    public static void main(String[] args) {
        SpringApplication.run(UlysseprodApplication.class, args);
    }

}
