package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "PROJECT_TBL")
@Data
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROJECT_ID")
    private Long projectId;

    @Column(name = "PROJECT_NAME", nullable = false, length = 255)
    private String projectName;

    @Column(name = "DESCRIPTION", length = 1024)
    private String description;

    @ManyToMany
    @JoinTable(
            name = "EMPLOYEE_PROJECT_TBL",
            joinColumns = @JoinColumn(name = "PROJECT_ID"),
            inverseJoinColumns = @JoinColumn(name = "EMPLOYEE_ID")
    )
    private Set<Employee> employees = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "PROJECT_SKILL_TBL",
            joinColumns = @JoinColumn(name = "PROJECT_ID"),
            inverseJoinColumns = @JoinColumn(name = "SKILL_ID")
    )
    private Set<Skill> skills = new HashSet<>();
}
