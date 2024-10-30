package com.example.EmployeeDB;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.example.EmployeeDB.entity")
public class EmployeeDbApplication {

    public static void main(String[] args) {

        SpringApplication.run(EmployeeDbApplication.class, args);

    }
}

