package com.example.EmployeeDB;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.EmployeeDB.repository")
@ComponentScan(basePackages = "com.example.EmployeeDB")
public class EmployeeDbApplication {

    public static void main(String[] args) {

        SpringApplication.run(EmployeeDbApplication.class, args);

    }
}

