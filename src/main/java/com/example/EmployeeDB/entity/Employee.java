package com.example.EmployeeDB.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "EMPLOYEE_TBL")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"projects", "skills"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMPLOYEE_ID")
    @EqualsAndHashCode.Include
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

    @ManyToMany(mappedBy = "employees")
    @JsonIgnoreProperties({"employees", "skills"})
    private Set<Project> projects = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "EMPLOYEE_SKILL_TBL",
            joinColumns = @JoinColumn(name = "EMPLOYEE_ID"),
            inverseJoinColumns = @JoinColumn(name = "SKILL_ID")
    )
    @JsonIgnoreProperties({"employees", "projects"})
    private Set<Skill> skills = new HashSet<>();
}
