package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "EMPLOYEE_TBL")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMPLOYEE_ID")
    private Long employeeId;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DATE_OF_BIRTH", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "AVATAR_URL")
    private String avatarUrl;

    @Column(name = "JOB_ROLE", nullable = false)
    private String jobRole;

    @Column(name = "GENDER", nullable = false)
    private String gender;

    @Column(name = "AGE")
    private Integer age;

    @Column(name = "EMAIL")
    private String email;
}
