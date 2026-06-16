package com.customs.customsdemo;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProcessApplication
public class CustomsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomsApplication.class, args);
    }
}
